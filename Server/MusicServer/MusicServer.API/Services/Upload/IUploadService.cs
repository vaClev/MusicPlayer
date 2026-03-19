namespace MusicServer.API.Services.Upload
{
    public interface IUploadService
    {
        public string GetExtensionWithCheck(IFormFile file);
        public string CreateFilePath(string fileName);
        public Task SaveFile(IFormFile file, string saveToFilepath);
        public void DeleteFile(string filepath);
    }
}