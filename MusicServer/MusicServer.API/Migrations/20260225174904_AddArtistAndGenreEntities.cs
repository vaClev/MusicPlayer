using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace MusicServer.API.Migrations
{
    /// <inheritdoc />
    public partial class AddArtistAndGenreEntities : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "artist",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "genre",
                table: "MusicFiles");

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

            migrationBuilder.AddColumn<int>(
                name: "artistId",
                table: "MusicFiles",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "genreId",
                table: "MusicFiles",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "Artists",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "character varying(200)", maxLength: 200, nullable: false),
                    Bio = table.Column<string>(type: "character varying(1000)", maxLength: 1000, nullable: true),
                    Country = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Artists", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Genres",
                columns: table => new
                {
                    Id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    Name = table.Column<string>(type: "character varying(100)", maxLength: 100, nullable: false),
                    Description = table.Column<string>(type: "character varying(500)", maxLength: 500, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Genres", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MusicFiles_artistId",
                table: "MusicFiles",
                column: "artistId");

            migrationBuilder.CreateIndex(
                name: "IX_MusicFiles_genreId",
                table: "MusicFiles",
                column: "genreId");

            migrationBuilder.CreateIndex(
                name: "IX_MusicFiles_Title",
                table: "MusicFiles",
                column: "title");

            migrationBuilder.CreateIndex(
                name: "IX_Artists_Name",
                table: "Artists",
                column: "Name");

            migrationBuilder.CreateIndex(
                name: "IX_Genres_Name",
                table: "Genres",
                column: "Name");

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

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Artists_artistId",
                table: "MusicFiles");

            migrationBuilder.DropForeignKey(
                name: "FK_MusicFiles_Genres_genreId",
                table: "MusicFiles");

            migrationBuilder.DropTable(
                name: "Artists");

            migrationBuilder.DropTable(
                name: "Genres");

            migrationBuilder.DropIndex(
                name: "IX_MusicFiles_artistId",
                table: "MusicFiles");

            migrationBuilder.DropIndex(
                name: "IX_MusicFiles_genreId",
                table: "MusicFiles");

            migrationBuilder.DropIndex(
                name: "IX_MusicFiles_Title",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "ArtistId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "GenreId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "artistId",
                table: "MusicFiles");

            migrationBuilder.DropColumn(
                name: "genreId",
                table: "MusicFiles");

            migrationBuilder.AddColumn<string>(
                name: "artist",
                table: "MusicFiles",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "genre",
                table: "MusicFiles",
                type: "text",
                nullable: true);
        }
    }
}
