package org.example.vasilev.musicpro.models;

public enum ExtraFileType
{
    SHEET_MUSIC(0, "Ноты"),
    TABS(1, "Табулатуры"),
    LYRICS(2, "Текст песни"),
    CHORDS(3, "Аккорды"),
    IMAGE(4, "Обложка/фото"),
    OTHER(5, "Прочее");

    private final int value;
    private final String description;

    ExtraFileType(int value, String description)
    {
        this.value = value;
        this.description = description;
    }

    /// JSON десериализация из int
    public static ExtraFileType fromValue(int value)
    {
        for (ExtraFileType type : values())
        {
            if (type.value == value)
                return type;
        }
        return OTHER;
    }

    public int getValue()
    {
        return value;
    }

    public String getDisplayName()
    {
        return description;
    }
}
