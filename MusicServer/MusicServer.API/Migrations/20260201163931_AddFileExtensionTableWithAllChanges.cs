using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace MusicServer.API.Migrations
{
    /// <inheritdoc />
    public partial class AddFileExtensionTableWithAllChanges : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "FileExtensionId",
                table: "MusicFiles",
                type: "integer",
                nullable: true);

            migrationBuilder.AddColumn<int>(
                name: "FileExtensionId",
                table: "ExtraFiles",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "FileExtensions",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Extension = table.Column<string>(type: "character varying(10)", maxLength: 10, nullable: false),
                    MimeType = table.Column<string>(type: "character varying(50)", maxLength: 50, nullable: false),
                    Category = table.Column<string>(type: "character varying(20)", maxLength: 20, nullable: false),
                    Description = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_FileExtensions", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MusicFiles_FileExtensionId",
                table: "MusicFiles",
                column: "FileExtensionId");

            migrationBuilder.CreateIndex(
                name: "IX_ExtraFiles_FileExtensionId",
                table: "ExtraFiles",
                column: "FileExtensionId");

            migrationBuilder.AddForeignKey(
                name: "FK_ExtraFiles_FileExtensions_FileExtensionId",
                table: "ExtraFiles",
                column: "FileExtensionId",
                principalTable: "FileExtensions",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_MusicFiles_FileExtensions_FileExtensionId",
                table: "MusicFiles",
                column: "FileExtensionId",
                principalTable: "FileExtensions",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_ExtraFiles_FileExtensions_FileExtensionId",
                table: "ExtraFiles");

            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_FileExtensions_FileExtensionId",
                table: "MusicFiles");

            migrationBuilder.DropTable(
                name: "FileExtensions");

            migrationBuilder.DropIndex(
                name: "IX_MusicFiles_FileExtensionId",
                table: "MusicFiles");

            migrationBuilder.DropIndex(
                name: "IX_ExtraFiles_FileExtensionId",
                table: "ExtraFiles");

            migrationBuilder.DropColumn(
                name: "FileExtensionId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "FileExtensionId",
                table: "ExtraFiles");
        }
    }
}
