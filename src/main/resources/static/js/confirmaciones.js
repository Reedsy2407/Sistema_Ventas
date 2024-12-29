// confirmaciones.js
document.addEventListener("DOMContentLoaded", function () {
  const deleteButtons = document.querySelectorAll(".btn-danger");

  // Obtener el ID del usuario autenticado desde un atributo de datos
  const usuarioLogueadoId = document.body.getAttribute("data-usuario-id");

  deleteButtons.forEach(button => {
    button.addEventListener("click", function (event) {
      event.preventDefault(); // Prevenir el comportamiento por defecto del enlace

      const href = this.getAttribute("href"); // Obtener la URL de eliminación
      const usuarioEliminarId = this.getAttribute("data-usuario-id");

      // Verificar si el usuario está intentando eliminarse a sí mismo
      if (usuarioLogueadoId === usuarioEliminarId) {
        Swal.fire({
          title: 'Acción no permitida',
          text: 'No puedes eliminar tu propio usuario.',
          icon: 'error',
          confirmButtonColor: '#3085d6',
          confirmButtonText: 'Aceptar'
        });
        return; // Detener la ejecución
      }

      // Confirmación estándar
      Swal.fire({
        title: '¿Estás seguro?',
        text: "Esta acción no se puede deshacer",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
      }).then((result) => {
        if (result.isConfirmed) {
          // Redirigir a la URL de eliminación si se confirma
          window.location.href = href;
        }
      });
    });
  });
});
