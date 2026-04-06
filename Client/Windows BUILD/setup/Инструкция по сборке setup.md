*полная инструкция "от и до"** для будущих сборок:

## 📦 **Полный цикл сборки клиента Windows MusicPro**

### **Шаг 1: Очистить предыдущую сборку**
```bash
cd C:\Projects\CourseWork1\MusicPro
mvn clean
```

### **Шаг 2: Удалить старый конфиг **
Нужно, чтобы приложение создало новый `musicplayer.properties` со стандартными настройками:
```bash
# Удали существующий файл (если есть)
rm -f musicplayer.properties
```

### **Шаг 3: Собрать проект с зависимостями**
Собирать нужно с правильной JAVA - version 21.0.9 как в проекте. 
```bash
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
export PATH="$JAVA_HOME/bin:$PATH"

# Проверь, что теперь всё правильно
java -version
mvn --version
```
```bash
mvn package
```
После этого в папке `target` появится `MusicPro-1.0.jar`.

### **Шаг 4: Создать runtime образ с нужными модулями**
```bash
cd desktop

jlink --module-path "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot\jmods" \
      --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported \
      --output target/runtime
```
В папке `target/runtime` появится мини-Java с необходимыми модулями.

### **Шаг 5: Собрать установщик**
```bash
# Очисти папку output от старых файлов
rm -rf output/*

# Собери установщик
jpackage --type exe \
         --input target \
         --name "MusicPro" \
         --main-jar musicpro-desktop-1.0.jar \
         --main-class org.example.vasilev.musicpro.desktop.launcher.Launcher \
         --runtime-image target/runtime \
         --win-dir-chooser \
         --win-menu \
         --win-shortcut \
         --win-shortcut-prompt \
         --dest output
```

### **Шаг 6: Готово!**
Установщик лежит в папке `output/MusicPro-1.0.exe`. Запускай и пользуйся! 🚀

## 📝 **Памятка для быстрого копирования (одним блоком):**.
```bash
cd C:\Projects\CourseWork1\Client\MusicPro
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
export PATH="$JAVA_HOME/bin:$PATH"
mvn clean
rm -f musicplayer.properties
mvn package
cd desktop
jlink --module-path "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot\jmods;C:\JAVA\javafx-jmods-21.0.9" --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported,javafx.controls,javafx.fxml,javafx.media --output target/runtime
rm -rf output/*
jpackage --type exe --input target --name "MusicPro" --main-jar musicpro-desktop-1.0.jar --main-class org.example.vasilev.musicpro.desktop.launcher.Launcher --runtime-image target/runtime --win-dir-chooser --win-menu --win-shortcut --win-shortcut-prompt --dest output
```