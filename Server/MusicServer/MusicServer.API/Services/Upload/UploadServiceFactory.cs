namespace MusicServer.API.Services.Upload
{
    public class UploadServiceFactory : IUploadServiceFactory
    {
        private readonly IServiceProvider _serviceProvider;
        public UploadServiceFactory(IServiceProvider serviceProvider)
        {
            _serviceProvider = serviceProvider;
        }
        public IUploadService Create(string storagePath, string[] allowedExtensions)
        {
            return new UploadService(
                storagePath,
                allowedExtensions);
        }
    }
}
