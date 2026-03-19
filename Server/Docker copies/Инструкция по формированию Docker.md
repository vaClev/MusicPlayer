Инструкция по запуску MusicPlayer API в Docker

1. Предварительные требования
1.1 Установите Docker Desktop
Скачайте Docker Desktop с официального сайта
Запустите установщик и следуйте инструкциям

Важно: Во время установки отметьте опцию "Use WSL 2 instead of Hyper-V"
После установки перезагрузите компьютер

1.2 Установите WSL 2 (если не установлен)
Откройте PowerShell от имени администратора и выполните:
```powershell
# Установка WSL
wsl --install

# Проверка версии (должна быть 2)
wsl --version

# Если версия не 2, установите её по умолчанию
wsl --set-default-version 2
```

2. Запуск приложения
2.1 Клонируйте репозиторий
```bash
git clone https://github.com/vaClev/MusicPlayer.git
cd MusicPlayer
```

2.2 Создайте файл .env с паролем для базы данных
В папке MusicPlayer создайте файл .env со следующим содержимым:
```
DB_PASSWORD=YourStrongPassword123!
```
Важно! Файл .env не должен попасть в git (он уже в .gitignore). Пароль указывается свой после =.

2.3 Запустите контейнеры
```powershell
# Убедитесь, что Docker Desktop запущен (значок в трее)
# Откройте PowerShell в папке с проектом (где лежит docker-compose.yml)

# Запуск контейнеров
docker-compose up -d
```

2.4 Проверка успешного запуска
```powershell
# Проверьте статус контейнеров
docker-compose ps
```
Должно быть в ответ:
```
     Name                 Command               State           Ports
-----------------------------------------------------------------------------------
music_api      dotnet MusicServer.API.dll      Up      0.0.0.0:5098->5098/tcp
music_postgres docker-entrypoint.sh postgres   Up      0.0.0.0:5433->5432/tcp
```

3. Проверка работоспособности
3.1 Проверьте API

```
# Откройте в браузере
http://localhost:5098/api/music

# Или через curl
curl http://localhost:5098/api/music
```

На этом этапе развертывание серверной части завершено.


------
Диагностика и дополнительная информация: 
------

3.2 Проверьте базу данных (если есть конфликты портов, или какие-то проблемы)
```powershell
# Подключение к PostgreSQL
docker exec -it music_postgres psql -U postgres -d MusicDb -c "\dt"
```
Должны увидеть список таблиц (MusicFiles, Artists, Genres и т.д.)


4. Подключение через pgAdmin (для администрирования)
Запустите pgAdmin 4
Создайте новое подключение:
Name: MusicPlayer Docker
Host: localhost
Port: 5433
Maintenance database: postgres
Username: postgres
Password: ваш пароль из .env

5. Полезные команды
powershell
# Просмотр логов
docker-compose logs -f

# Остановка контейнеров
docker-compose stop

# Запуск после остановки
docker-compose start

# Полная остановка с удалением контейнеров
docker-compose down

# Полная перезагрузка (если что-то пошло не так)
docker-compose down -v
docker-compose up -d
6. Известные ограничения
Образ API не включен в репозиторий по причине большого размера (около 250 МБ). При первом запуске docker-compose up -d образ будет собран автоматически из исходного кода. Сборка занимает 5-10 минут в зависимости от скорости интернета и компьютера.

7. Возможные проблемы и решения


Проблема: порт 5433 уже занят
Измените порт в docker-compose.yml:
```yaml
ports:
  - "5432:5432"  # замените на любой свободный порт (5433:5432) (5435:5432)
```

Проблема: Docker не запускается
Убедитесь, что:
Docker Desktop запущен (значок в трее)
WSL 2 установлен и настроен
Виртуализация включена в BIOS


Проблема: база данных не создается
```powershell
# Принудительно пересоздать базу
docker-compose down -v
docker-compose up -d
```

8. Структура файлов для успешного запуска
Убедитесь, что в папке проекта есть все необходимые файлы:

```
MusicPlayer/
├── docker-compose.yml
├── .env (создать самостоятельно)
├── MusicServer/
│   └── MusicServer.API/
│       ├── Dockerfile
│       ├── appsettings.json
│       ├── appsettings.Docker.json
│       └── ... (остальные файлы проекта)
```

9. Проверка версий (если возникают сомнения)
```powershell
docker --version
docker-compose --version
wsl --version
```