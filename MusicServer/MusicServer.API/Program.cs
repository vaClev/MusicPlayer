using Microsoft.AspNetCore.Http.Features;
using Microsoft.EntityFrameworkCore;
using MusicServer.API;
using MusicServer.API.Database;

/////////////////////////////////////////////////////
/// конфигурация билдера
///
////////////////////////////////////////////////////
var builder = WebApplication.CreateBuilder(args); //Построитель приложения по шаблону dotnetWebApi

// Сохраняем путь к файлам в конфигурации
builder.Configuration["MusicStorage:FullPath"] = AppConfigUtils.InitMusicFolder(builder.Configuration["MusicStorage:Path"]);

// ВАЖНО: Настройка лимитов ДО добавления контроллеров
builder.Services.Configure<FormOptions>(options =>
{
    options.ValueLengthLimit = int.MaxValue;
    options.MultipartBodyLengthLimit = 104857600; // 100MB
    options.MemoryBufferThreshold = int.MaxValue;
});

// Добавляем контроллеры
builder.Services.AddControllers();

// Настраиваем PostgreSQL - в рамках разработки курсовой подключение к локальноve серверу БД
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DevelopConnection")));

// Регистрируем MusicService сервис в приложении
AppConfigUtils.RegistrateMusicService(builder);

//#ifdef __DEBUG/////////////////////////////////////////////////////////////////////////////
// Разрешаем CORS для клиента -- чтобы в целях тестирования отправлять запросы с одного компa
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        policy =>
        {
            policy.AllowAnyOrigin()     // Разрешить ВСЕ источники (опасно для продакшена!)
                  .AllowAnyMethod()
                  .AllowAnyHeader()
                  .WithExposedHeaders("Content-Disposition"); // Важно для скачивания!
        });
});
////////////////////////////////////////////////////////////////////////////////////////////

// для тестирования API
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddOpenApi();


/////////////////////////////////////////////////////
/// создание приложения
///
////////////////////////////////////////////////////
var app = builder.Build();


/////////////////////////////////////////////////////
/// конфигурация приложения
///
////////////////////////////////////////////////////
// Конфигурация middleware - для тестирования Post запроса
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();
app.UseCors("AllowAll");
app.UseAuthorization();
app.MapControllers();

// Инициализация базы данных
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    dbContext.Database.EnsureCreated(); // Создает БД если её нет
}

app.Run();
