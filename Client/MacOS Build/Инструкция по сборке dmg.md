## 📦 **Полный цикл сборки клиента MacOS MusicPro **
## Выполнено на macOS Monterey 12.5

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

  #Скачать JDK 21 можно с Adoptium или через
  brew install openjdk@21.
```
----

### **Шаг 4: Создать runtime образ с нужными модулями**

Узнать путь к JDK на Mac:
```bash
/usr/libexec/java_home -v 21
```
Далее используя путь выполняем jlink. Например:
```bash
jlink --module-path "/Users/aleksandravasileva/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/jmods" \
      --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported \
      --output target/runtime
```
В папке `target/runtime` появится мини-Java с необходимыми модулями.


### **Шаг 5: Собрать установщик .dmg **
```bash
# Очисти папку output от старых файлов
rm -rf output/*

# Собери установщик .dmg
```bash
jpackage --type dmg \
         --input target \
         --name "MusicPro" \
         --main-jar MusicPro-1.0.jar \
         --main-class org.example.vasilev.musicpro.Launcher \
         --runtime-image target/runtime \
         --icon MusicPro.icns \  # если есть иконка, иначе без --icon
         --mac-package-identifier com.yourcompany.musicpro \
         --mac-package-name "MusicPro" \
         --dest output
```

### **Шаг 6: Готово!**
Установщик лежит в папке `output/MusicPro-1.0.dmg`. Запускай и пользуйся! 🚀

У меня он сам даже запустился, перетаскиваем MusicPro в application и все.

## 📝 **Вариант для быстрого создания dmg (одним блоком):**
```
mvn clean && mvn package && rm -rf target/runtime && jlink --module-path "/Users/aleksandravasileva/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home/jmods" --add-modules java.base,java.desktop,java.logging,java.scripting,java.sql,java.xml,jdk.jfr,jdk.unsupported --output target/runtime && jpackage --type dmg --input target --name "MusicPro" --main-jar MusicPro-1.0.jar --main-class org.example.vasilev.musicpro.Launcher --runtime-image target/runtime --dest output
```
