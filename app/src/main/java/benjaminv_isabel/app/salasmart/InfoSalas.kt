package benjaminv_isabel.app.salasmart

data class InfoAula(
    val nombre: String,
    val ubicacion: String,
    val foto: Int,
    val asientos: Int,
    val tipo: String = ""
)

// Nombre real, ubicación, foto y cantidad de asientos de cada sala
val Aulas = mapOf(
    "Sala 1" to InfoAula("Aula Magna", "Edificio A • Piso 1", R.drawable.sala1, 40, "Aula"),
    "Sala 2" to InfoAula("Laboratorio de Computación", "Edificio B • Piso 2", R.drawable.sala2, 20, "Laboratorio"),
    "Sala 3" to InfoAula("Sala de Estudio", "Biblioteca • Piso 1", R.drawable.sala3, 8, "Estudio"),
    "Sala 4" to InfoAula("Auditorio Central", "Edificio C • Piso 1", R.drawable.sala4, 60, "Auditorio"),
    "Sala 5" to InfoAula("Biblioteca", "Edificio A • Piso 2", R.drawable.sala5, 24, "Biblioteca"),
    "Sala 6" to InfoAula("Sala de Reuniones", "Edificio D • Piso 3", R.drawable.sala6, 12, "Reuniones")
)