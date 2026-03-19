namespace MusicServer.API.Services.Upload
{
    public interface IUploadServiceFactory
    {
        IUploadService Create(string storagePath, string[] allowedExtensions);
    }

}
