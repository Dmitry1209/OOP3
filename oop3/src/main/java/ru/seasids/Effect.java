package ru.seasids;

import lombok.Data;
import javax.persistence.*;

@Data
@Embeddable
public class Effect {
    private String effectName;
    private String effectType;

    public Effect() {} // Пустой конструктор для Hibernate

    public Effect(String effectName, String effectType) {
        this.effectName = effectName;
        this.effectType = effectType;
    }

    @Override
    public String toString() {
        return effectName + " (тип: " + effectType + ")";
    }
}