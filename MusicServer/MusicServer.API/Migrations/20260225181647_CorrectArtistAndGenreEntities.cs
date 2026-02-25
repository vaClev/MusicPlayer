using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MusicServer.API.Migrations
{
    /// <inheritdoc />
    public partial class CorrectArtistAndGenreEntities : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Artists_artistId",
                table: "MusicFiles");

            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Genres_genreId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "ArtistId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "GenreId",
                table: "MusicFiles");

            migrationBuilder.RenameColumn(
                name: "genreId",
                table: "MusicFiles",
                newName: "GenreId");

            migrationBuilder.RenameColumn(
                name: "artistId",
                table: "MusicFiles",
                newName: "ArtistId");

            migrationBuilder.RenameIndex(
                name: "IX_MusicFiles_genreId",
                table: "MusicFiles",
                newName: "IX_MusicFiles_GenreId");

            migrationBuilder.RenameIndex(
                name: "IX_MusicFiles_artistId",
                table: "MusicFiles",
                newName: "IX_MusicFiles_ArtistId");

            migrationBuilder.AlterColumn<int>(
                name: "GenreId",
                table: "MusicFiles",
                type: "integer",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "integer",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_MusicFiles_Artists_ArtistId",
                table: "MusicFiles",
                column: "ArtistId",
                principalTable: "Artists",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_MusicFiles_Genres_GenreId",
                table: "MusicFiles",
                column: "GenreId",
                principalTable: "Genres",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Artists_ArtistId",
                table: "MusicFiles");

            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Genres_GenreId",
                table: "MusicFiles");

            migrationBuilder.RenameColumn(
                name: "GenreId",
                table: "MusicFiles",
                newName: "genreId");

            migrationBuilder.RenameColumn(
                name: "ArtistId",
                table: "MusicFiles",
                newName: "artistId");

            migrationBuilder.RenameIndex(
                name: "IX_MusicFiles_GenreId",
                table: "MusicFiles",
                newName: "IX_MusicFiles_genreId");

            migrationBuilder.RenameIndex(
                name: "IX_MusicFiles_ArtistId",
                table: "MusicFiles",
                newName: "IX_MusicFiles_artistId");

            migrationBuilder.AlterColumn<int>(
                name: "genreId",
                table: "MusicFiles",
                type: "integer",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.AddColumn<int>(
                name: "ArtistId",
                table: "MusicFiles",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "GenreId",
                table: "MusicFiles",
                type: "integer",
                nullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_MusicFiles_Artists_artistId",
                table: "MusicFiles",
                column: "artistId",
                principalTable: "Artists",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_MusicFiles_Genres_genreId",
                table: "MusicFiles",
                column: "genreId",
                principalTable: "Genres",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }
    }
}
