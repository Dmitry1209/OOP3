package ru.seasids;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "electric_guitars")
@PrimaryKeyJoinColumn(name = "id")
public class ElectricGuitar extends Guitar {
    private int numberOfPickups;
    private boolean hasTremoloSystem;

    @ElementCollection
    @CollectionTable(name = "guitar_effects", joinColumns = @JoinColumn(name = "guitar_id"))
    private List<Effect> effects = new ArrayList<>();

    @Override
    public String getType() {
        return "Electric";
    }

    public void playGuitar() {
        System.out.println("Игра на электрогитаре '" + getBrand() + "' с " + getNumberOfStrings() + " струнами.");
    }
    public void tuneGuitar() {
        System.out.println("Настройка электрогитары '" + getBrand() + "'. Применён стандартный строй.");
    }
    // Уникальный метод для настройки гитары
    public void addEffect(String effectName, String effectType) {
        this.effects.add(new Effect(effectName, effectType));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(getId()).append("\n");
        sb.append("Электрогитара: ").append(getBrand()).append("\n");
        sb.append("Количество струн: ").append(getNumberOfStrings()).append("\n");
        sb.append("Количество звукоснимателей: ").append(numberOfPickups).append("\n");
        sb.append("Наличие тремоло-системы: ").append(hasTremoloSystem ? "Да" : "Нет").append("\n");
        sb.append("Эффекты:\n");
        for (Effect effect : effects) {
            sb.append("- ").append(effect).append("\n");
        }
        return sb.toString();
    }
}