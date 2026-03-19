# Базовый образ для сборки
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

# Копируем файл проекта и восстанавливаем зависимости
COPY ["MusicServer.API.csproj", "."]
RUN dotnet restore "MusicServer.API.csproj"

# Копируем все исходники и собираем приложение
COPY . .
RUN dotnet publish "MusicServer.API.csproj" -c Release -o /app/publish

# Финальный образ для запуска
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS final
WORKDIR /app

# Устанавливаем библиотеки для TagLibSharp (для работы с аудиофайлами в Linux)
RUN apt-get update && apt-get install -y \
    libgdiplus \
    libc6-dev \
    && rm -rf /var/lib/apt/lists/*

# Копируем собранное приложение
COPY --from=build /app/publish .

# Создаем директории для загружаемых файлов
RUN mkdir -p /app/data/music /app/data/extra

# Открываем порты
EXPOSE 5098
EXPOSE 7044

# Запускаем приложение
ENTRYPOINT ["dotnet", "MusicServer.API.dll"]