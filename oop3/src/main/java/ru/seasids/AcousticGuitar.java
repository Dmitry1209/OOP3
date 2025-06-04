package ru.seasids;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "acoustic_guitars")
@PrimaryKeyJoinColumn(name = "id")
public class AcousticGuitar extends Guitar {

    private String woodType;
    private boolean hasPickup;

    @Override
    public String getType() {
        return "Acoustic";
    }

    // Уникальный метод для акустической гитары
    public void playGuitar() {
        System.out.println("Играет акустическая гитара '" + getBrand() + "' с " + getNumberOfStrings() + " струнами.");
    }
    // Уникальный метод для настройки гитары
    public void tuneGuitar() {
        System.out.println("Настройка акустической гитары '" + getBrand() + "'. Настроена в стандартный строй.");
    }
    @Override
    public String toString() {
        return  "ID: " + getId() + "\n" +
                "Акустическая гитара: " + getBrand() + "\n" +
                "Количество струн: " + getNumberOfStrings() + "\n" +
                "Тип дерева: " + woodType + "\n" +
                "Звукосниматель: " + (hasPickup ? "Да" : "Нет");
    }
}