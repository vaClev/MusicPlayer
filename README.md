Курсовая работа по .NET C#
Тема "Музыкальный плеер с удаленной библиотекой и возможностью хранения связанных нот, табулатур"

-----
API серверной части:
-----
1.  Получить всю коллекцию музыки  
GET: 
```
https://127.0.0.1:7044/api/Music
```

2. Получить конкретный музыкальный файл по id
GET: 
```
https://127.0.0.1:7044/api/music/id5
```

3.  Загрузить муз файл на сервер.
POST: 
```
https://127.0.0.1:7044/api/music/upload
```
```"AllowedExtensions": [ ".mp3", ".wav", ".flac", ".ogg" ]```

4.  Скачать муз файл с сервера.
GET:
 ```
https://127.0.0.1:7044/api/music/download/id5
```

-----------
Аналогично для доп файлов:
5.  Получить все связанные с муз файлом доп файлы
GET: 
```
https://127.0.0.1:7044/api/extrafiles/music/id5
```

6. Получить конкретный доп файл
GET:
```
https://127.0.0.1:7044/api/extrafiles/id5
```

7.  Загрузить доп файл на сервер.
POST: 
```
https://127.0.0.1:7044/api/extrafiles/upload
```
Тут важно использовать форму заполнив поля:
```html
<form enctype="multipart/form-data" method="post" action="http://127.0.0.1:5098/api/extrafiles/upload">
            <p>
                <input type="number" name = "MusicFileId"/>
                <input type="number" name = "FileType"/>
                <input type="text" name = "Description"/>
                <input type="file" name="file" />
            </p>
            <p>
                <input type="submit" value="Отправить" />
            </p>
        </form>
```
FileType указывать int - в соответсвии с Enum
```csharp
public enum ExtraFileType
{
    SheetMusic = 0,     // Ноты
    Tabs = 1,           // Табулатуры
    Lyrics = 2,         // Текст песни
    Chords = 3,         // Аккорды
    Image = 4,          // Обложка/фото
    Other =5          // Прочее
}
```
```
 "AllowedExtensions": [ ".pdf", ".txt", ".jpg", ".png", ".doc", ".docx", ".gpx", ".gp", ".gp3", ".gp4", ".gp5" ]
```
8.  Скачать доп файл с сервера.
GET:
```
https://127.0.0.1:7044/api/extrafiles/id5
```
