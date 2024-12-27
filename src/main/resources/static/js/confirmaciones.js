// confirmaciones.js
document.addEventListener("DOMContentLoaded", function () {
  // Seleccionar todos los enlaces con clase btn-danger
  const deleteButtons = document.querySelectorAll(".btn-danger");

  deleteButtons.forEach(button => {
    button.addEventListener("click", function (event) {
      event.preventDefault(); // Prevenir el comportamiento por defecto del enlace

      const href = this.getAttribute("href"); // Obtener la URL de eliminación

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
