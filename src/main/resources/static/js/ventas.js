document.addEventListener("DOMContentLoaded", function () {
    // Referenciar elementos clave
    const cantidades = document.querySelectorAll(".cantidad");
    const totalField = document.getElementById("total");

    // Función para actualizar subtotales y total
    const actualizarTotales = () => {
        let total = 0;

        // Iterar sobre cada campo de cantidad
        cantidades.forEach(cantidadField => {
            const productoId = cantidadField.getAttribute("data-producto-id");
            const precioField = cantidadField.closest(".row").querySelector(".precio");
            const subtotalField = document.getElementById("subtotal" + productoId);

            const cantidad = parseFloat(cantidadField.value) || 0;
            const precio = parseFloat(precioField.value) || 0;
            const subtotal = cantidad * precio;

            // Actualizar el subtotal
            subtotalField.value = subtotal.toFixed(2);
            total += subtotal;
        });

        // Actualizar el total general
        totalField.value = total.toFixed(2);
    };

    // Asignar el evento `input` a cada campo de cantidad
    cantidades.forEach(cantidadField => {
        cantidadField.addEventListener("input", actualizarTotales);
    });
});
