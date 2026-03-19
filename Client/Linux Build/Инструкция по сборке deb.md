## 📦 **Полный цикл сборки клиента Linux MusicPro **
## Выполнено на Linux Ubuntu 24.0.4

### **Шаг 1: Клонировать репозиторий **
```bash
git clone https://github.com/vaClev/MusicPlayer.git
cd MusicPlayer
```

### **Шаг 2: Удалить старый конфиг **
Нужно, чтобы приложение создавало новый `musicplayer.properties` со стандартными настройками:
```bash
# Удали существующий файл (если есть)
rm -f musicplayer.properties
```

### **Шаг 3: Собрать проект с зависимостями**
В идеале открыть папку MusicPro как проект в IntellijIDEA.
При запуске подтянуть зависимости и нужный JDK.
Использовать maven plugin и в нем выполнитьь 2 цели :  clean и package.

Либо если maven установлен глобально можно в терминале:
```bash
mvn clean
mvn package
```

После этого в папке `target` появится `MusicPro-1.0.jar`.


----
```bash
#Примечание: 
  #Технически `MusicPro-1.0.jar` можно получить и на windows. 
  #Но придется тогда вручную скачивать нужные версии JDK, для шага 4.

  #Скачать JDK 21 можно c apt
  sudo apt install openjdk-21-jdk -y
```
----

### **Шаг 4: Создать runtime образ с нужными модулями**

Узнать путь к JDK на Linux:
```bash
which java
```
Далее используя путь выполняем jlink. Например:
```bash
jlink --module-path "..../openjdk-21.0.1/Contents/Home/jmods" \
      --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported \
      --output target/runtime
```
```
or with JAVA_HOME
jlink --module-path "$JAVA_HOME/jmods" \
      --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported \
      --output target/runtime
```
В папке `target/runtime` появится мини-Java с необходимыми модулями.


### **Шаг 5: Собрать установщик .dmg **
```bash
# Очисти папку output от старых файлов
rm -rf output/*

# Собери установщик .deb
```bash
jpackage --type deb \
         --input target \
         --name "MusicPro" \
         --main-jar MusicPro-1.0.jar \
         --main-class org.example.vasilev.musicpro.Launcher \
         --runtime-image target/runtime \
         --linux-shortcut \
         --linux-menu-group "AudioVideo;Audio;" \
         --linux-app-category "sound" \
         --vendor "Oleg Vasilev" \
         --app-version "1.0" \
         --dest output
```

### **Шаг 6: Готово!**
Установщик лежит в папке `output/musicpro_1.0_amd64.deb`. Запускай ! 🚀

```bash
sudo dpkg -i output/musicpro_1.0_amd64.deb
```

7. After setup
File .properties need copy to 
```bash
~/test/MusicPlayer
```
