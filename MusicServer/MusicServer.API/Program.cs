using Microsoft.EntityFrameworkCore;
using MusicServer.API.Database;

/////////////////////////////////////////////////////
/// конфигурация приложения
///
////////////////////////////////////////////////////
var builder = WebApplication.CreateBuilder(args); //Построитель приложения по шаблону dotnetWebApi

// Добавляем контроллеры
builder.Services.AddControllers();

// Настраиваем PostgreSQL - в рамках разработки курсовой подключение к локальноve серверу БД
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DevelopConnection")));

//#ifdef __DEBUG/////////////////////////////////////////////////////////////////////////////
// Разрешаем CORS для клиента -- чтобы в целях тестирования отправлять запросы с одного компa
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        policy =>
        {
            policy.AllowAnyOrigin()     // Разрешить ВСЕ источники (опасно для продакшена!)
                  .AllowAnyMethod()
                  .AllowAnyHeader();
        });
});
////////////////////////////////////////////////////////////////////////////////////////////

// для тестирования API
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddOpenApi();

var app = builder.Build();

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
