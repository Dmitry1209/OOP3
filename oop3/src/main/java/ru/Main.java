package ru;

import org.hibernate.Session;
import ru.seasids.dao.GenericDAO;
import ru.seasids.dao.GuitarDAO;
import ru.seasids.Guitar;
import ru.seasids.AcousticGuitar;
import ru.seasids.ElectricGuitar;
import ru.seasids.Effect;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GuitarDAO dao = new GuitarDAO();

    public static void main(String[] args) {
        try {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1 -> addNewGuitar();
                    case 2 -> updateGuitar();
                    case 3 -> deleteGuitar();
                    case 4 -> additionalActions();
                    case 5 -> findElectricGuitarsByEffect();
                    case 6 -> running = false;
                    default -> System.out.println("Неверный выбор");
                }
            }
        } finally {
            GenericDAO.shutdown();
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Управление гитарами ===");
        System.out.println("1. Добавить новую гитару");
        System.out.println("2. Изменить существующую");
        System.out.println("3. Удалить гитару");
        System.out.println("4. Дополнительные действия");
        System.out.println("5. Найти электрогитары по эффекту");
        System.out.println("6. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void addNewGuitar() {
        System.out.println("\nВыберите тип:");
        System.out.println("1. Акустическая гитара");
        System.out.println("2. Электрогитара");
        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите бренд: ");
        String brand = scanner.nextLine();

        System.out.print("Введите количество струн: ");
        int numberOfStrings = scanner.nextInt();
        scanner.nextLine();

        if (type == 1) {
            AcousticGuitar guitar = new AcousticGuitar();
            guitar.setBrand(brand);
            guitar.setNumberOfStrings(numberOfStrings);

            System.out.print("Введите тип дерева: ");
            String woodType = scanner.nextLine();
            guitar.setWoodType(woodType);

            System.out.print("Есть ли звукосниматель? (да/нет): ");
            boolean hasPickup = scanner.nextLine().equalsIgnoreCase("да");
            guitar.setHasPickup(hasPickup);

            dao.save(guitar);
            System.out.println("Акустическая гитара успешно добавлена! ID: " + guitar.getId());
        } else {
            ElectricGuitar guitar = new ElectricGuitar();
            guitar.setBrand(brand);
            guitar.setNumberOfStrings(numberOfStrings);

            System.out.print("Введите количество звукоснимателей: ");
            int numberOfPickups = scanner.nextInt();
            scanner.nextLine();
            guitar.setNumberOfPickups(numberOfPickups);

            System.out.print("Есть ли тремоло-система? (да/нет): ");
            boolean hasTremoloSystem = scanner.nextLine().equalsIgnoreCase("да");
            guitar.setHasTremoloSystem(hasTremoloSystem);

            System.out.println("Введите эффекты (для завершения введите 'end'):");
            while (true) {
                System.out.print("Название эффекта: ");
                String effectName = scanner.nextLine();
                if (effectName.equalsIgnoreCase("end")) {
                    break;
                }
                System.out.print("Тип эффекта: ");
                String effectType = scanner.nextLine();
                guitar.addEffect(effectName, effectType);
            }

            dao.save(guitar);
            System.out.println("Электрогитара успешно добавлена! ID: " + guitar.getId());
        }
    }

    private static void updateGuitar() {
        System.out.print("\nВведите ID гитары: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try (Session session = GenericDAO.getSessionFactory().openSession()) {
            Guitar guitar = session.get(Guitar.class, id);
            if (guitar == null) {
                System.out.println("Гитара с ID " + id + " не найдена");
                return;
            }

            if (guitar instanceof AcousticGuitar acousticGuitar) {
                System.out.println("Выбрана акустическая гитара с ID: " + id);
                updateAcousticGuitar(acousticGuitar, session);
            } else if (guitar instanceof ElectricGuitar electricGuitar) {
                System.out.println("Выбрана электрогитара с ID: " + id);
                updateElectricGuitar(electricGuitar, session);
            }
        }
    }

    private static void updateAcousticGuitar(AcousticGuitar guitar, Session session) {
        System.out.println("\nТекущие данные:");
        System.out.println("Бренд: " + guitar.getBrand());
        System.out.println("Количество струн: " + guitar.getNumberOfStrings());
        System.out.println("Тип дерева: " + guitar.getWoodType());
        System.out.println("Звукосниматель: " + (guitar.isHasPickup() ? "Да" : "Нет"));

        System.out.print("\nНовый бренд (Enter - оставить прежний): ");
        String brand = scanner.nextLine();
        if (!brand.isEmpty()) guitar.setBrand(brand);

        System.out.print("Новое количество струн (0 - оставить прежнее): ");
        int numberOfStrings = scanner.nextInt();
        scanner.nextLine();
        if (numberOfStrings != 0) guitar.setNumberOfStrings(numberOfStrings);

        System.out.print("Новый тип дерева (Enter - оставить прежний): ");
        String woodType = scanner.nextLine();
        if (!woodType.isEmpty()) guitar.setWoodType(woodType);

        System.out.print("Есть ли звукосниматель? (да/нет, Enter - оставить прежнее): ");
        String hasPickupInput = scanner.nextLine();
        if (!hasPickupInput.isEmpty()) guitar.setHasPickup(hasPickupInput.equalsIgnoreCase("да"));

        session.beginTransaction();
        session.update(guitar);
        session.getTransaction().commit();
        System.out.println("Данные акустической гитары обновлены!");
    }

    private static void updateElectricGuitar(ElectricGuitar guitar, Session session) {
        System.out.println("\nТекущие данные:");
        System.out.println("Бренд: " + guitar.getBrand());
        System.out.println("Количество струн: " + guitar.getNumberOfStrings());
        System.out.println("Количество звукоснимателей: " + guitar.getNumberOfPickups());
        System.out.println("Тремоло-система: " + (guitar.isHasTremoloSystem() ? "Да" : "Нет"));
        System.out.println("Эффекты:");
        for (Effect effect : guitar.getEffects()) {
            System.out.printf("- %s (тип: %s)%n", effect.getEffectName(), effect.getEffectType());
        }

        System.out.print("\nНовый бренд (Enter - оставить прежний): ");
        String brand = scanner.nextLine();
        if (!brand.isEmpty()) guitar.setBrand(brand);

        System.out.print("Новое количество струн (0 - оставить прежнее): ");
        int numberOfStrings = scanner.nextInt();
        scanner.nextLine();
        if (numberOfStrings != 0) guitar.setNumberOfStrings(numberOfStrings);

        System.out.print("Новое количество звукоснимателей (0 - оставить прежнее): ");
        int numberOfPickups = scanner.nextInt();
        scanner.nextLine();
        if (numberOfPickups != 0) guitar.setNumberOfPickups(numberOfPickups);

        System.out.print("Есть ли тремоло-система? (да/нет, Enter - оставить прежнее): ");
        String hasTremoloSystemInput = scanner.nextLine();
        if (!hasTremoloSystemInput.isEmpty()) guitar.setHasTremoloSystem(hasTremoloSystemInput.equalsIgnoreCase("да"));

        System.out.println("\nУправление эффектами:");
        System.out.println("1. Добавить эффект");
        System.out.println("2. Удалить эффект");
        System.out.println("3. Изменить тип эффекта");
        System.out.println("4. Пропустить");
        System.out.print("Выберите действие: ");
        int effectAction = scanner.nextInt();
        scanner.nextLine();

        switch (effectAction) {
            case 1:
                System.out.print("Название нового эффекта: ");
                String newEffectName = scanner.nextLine();
                System.out.print("Тип эффекта: ");
                String newEffectType = scanner.nextLine();
                guitar.addEffect(newEffectName, newEffectType);
                break;
            case 2:
                System.out.print("Введите название эффекта для удаления: ");
                String effectToRemove = scanner.nextLine();
                guitar.getEffects().removeIf(e -> e.getEffectName().equals(effectToRemove));
                break;
            case 3:
                System.out.print("Введите название эффекта: ");
                String effectName = scanner.nextLine();
                boolean effectExists = guitar.getEffects().stream()
                        .anyMatch(e -> e.getEffectName().equals(effectName));
                if (!effectExists) {
                    System.out.println("Эффект с названием '" + effectName + "' не найден");
                    break;
                }
                System.out.print("Новый тип эффекта: ");
                String newType = scanner.nextLine();
                guitar.getEffects().stream()
                        .filter(e -> e.getEffectName().equals(effectName))
                        .findFirst()
                        .ifPresent(e -> e.setEffectType(newType));
                break;
        }

        session.beginTransaction();
        session.update(guitar);
        session.getTransaction().commit();
        System.out.println("Данные электрогитары обновлены!");
    }

    private static void deleteGuitar() {
        System.out.println("\nВыберите тип для удаления:");
        System.out.println("1. Акустическая гитара");
        System.out.println("2. Электрогитара");
        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите ID гитары: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try (Session session = GenericDAO.getSessionFactory().openSession()) {
            if (type == 1) {
                AcousticGuitar guitar = session.get(AcousticGuitar.class, id);
                if (guitar != null) {
                    dao.delete(guitar);
                    System.out.println("Акустическая гитара успешно удалена!");
                } else {
                    System.out.println("Акустическая гитара с ID " + id + " не найдена");
                }
            } else {
                ElectricGuitar guitar = session.get(ElectricGuitar.class, id);
                if (guitar != null) {
                    dao.delete(guitar);
                    System.out.println("Электрогитара успешно удалена!");
                } else {
                    System.out.println("Электрогитара с ID " + id + " не найдена");
                }
            }
        }
    }

    private static void additionalActions() {
        System.out.println("\nВыберите тип гитары:");
        System.out.println("1. Акустическая гитара");
        System.out.println("2. Электрогитара");
        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите ID гитары: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try (Session session = GenericDAO.getSessionFactory().openSession()) {
            if (type == 1) {
                AcousticGuitar guitar = session.get(AcousticGuitar.class, id);
                if (guitar != null) {
                    acousticGuitarActions(guitar);
                } else {
                    System.out.println("Акустическая гитара с ID " + id + " не найдена");
                }
            } else {
                ElectricGuitar guitar = session.get(ElectricGuitar.class, id);
                if (guitar != null) {
                    electricGuitarActions(guitar);
                } else {
                    System.out.println("Электрогитара с ID " + id + " не найдена");
                }
            }
        }
    }

    private static void acousticGuitarActions(AcousticGuitar guitar) {
        System.out.println("\n=== Дополнительные действия для акустической гитары ===");
        System.out.println("1. Играть на гитаре");
        System.out.println("2. Настроить гитару");
        System.out.println("3. Показать информацию");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                guitar.playGuitar();
                break;
            case 2:
                guitar.tuneGuitar();
                break;
            case 3:
                System.out.println("\n" + guitar.toString());
                break;
            default:
                System.out.println("Неверный выбор");
        }
    }

    private static void electricGuitarActions(ElectricGuitar guitar) {
        System.out.println("\n=== Дополнительные действия для электрогитары ===");
        System.out.println("1. Играть на гитаре");
        System.out.println("2. Настроить гитару");
        System.out.println("3. Показать информацию");
        System.out.print("Выберите действие: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                guitar.playGuitar();
                break;
            case 2:
                guitar.tuneGuitar();
                break;
            case 3:
                System.out.println("\n" + guitar.toString());
                break;
            default:
                System.out.println("Неверный выбор");
        }
    }

    private static void findElectricGuitarsByEffect() {
        System.out.print("\nВведите название эффекта: ");
        String effectName = scanner.nextLine();

        try (Session session = GenericDAO.getSessionFactory().openSession()) {
            String hql = "SELECT eg FROM ElectricGuitar eg JOIN eg.effects e WHERE e.effectName = :effectName";
            List<ElectricGuitar> guitars = session.createQuery(hql, ElectricGuitar.class)
                    .setParameter("effectName", effectName)
                    .getResultList();

            if (guitars.isEmpty()) {
                System.out.println("Электрогитары с эффектом '" + effectName + "' не найдены");
            } else {
                System.out.println("Найдены электрогитары с эффектом '" + effectName + "':");
                for (ElectricGuitar guitar : guitars) {
                    System.out.println("\n" + guitar.toString());
                }
            }
        }
    }
}