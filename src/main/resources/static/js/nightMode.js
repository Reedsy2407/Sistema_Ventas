document.addEventListener('DOMContentLoaded', () => {
    const toggleButton = document.getElementById('toggle-night-mode');
    const toggleIcon = toggleButton.querySelector('img');

    // Verificar el estado almacenado en localStorage
    const nightModeEnabled = localStorage.getItem('nightMode') === 'true';

    if (nightModeEnabled) {
        enableNightMode();
        toggleIcon.src = '/images/sun-icon.png'; // Icono para cambiar al modo día
    } else {
        disableNightMode();
        toggleIcon.src = '/images/moon-icon.png'; // Icono para cambiar al modo noche
    }

    toggleButton.addEventListener('click', () => {
        const isNightMode = document.body.classList.toggle('night-mode');
        document.querySelector('.sidebar').classList.toggle('night-mode');
        document.querySelector('.content').classList.toggle('night-mode');

        // Guardar el estado en localStorage
        localStorage.setItem('nightMode', isNightMode);

        // Cambiar ícono con animación
        toggleIcon.classList.add('icon-transition');
        if (isNightMode) {
            enableNightMode();
            toggleIcon.src = '/images/sun-icon.png';
        } else {
            disableNightMode();
            toggleIcon.src = '/images/moon-icon.png';
        }
        // Remover clase después de la animación
        setTimeout(() => toggleIcon.classList.remove('icon-transition'), 300);
    });

function enableNightMode() {
    document.body.classList.add('night-mode');
    document.querySelector('.sidebar').classList.add('night-mode');
    document.querySelector('.content').classList.add('night-mode');

    // Cambiar estilos de inputs, selects y tablas en modo oscuro
    document.querySelectorAll('input.form-control').forEach(input => {
        input.classList.add('night-mode');
    });
    document.querySelectorAll('select.form-select').forEach(select => {
        select.classList.add('night-mode');
    });
    document.querySelectorAll('table.table').forEach(table => {
        table.classList.add('night-mode');
    });
}

    function disableNightMode() {
        document.body.classList.remove('night-mode');
        document.querySelector('.sidebar').classList.remove('night-mode');
        document.querySelector('.content').classList.remove('night-mode');

        // Revertir estilos de tablas y inputs en modo claro
        document.querySelectorAll('input.form-control').forEach(input => {
            input.classList.remove('night-mode');
        });
            document.querySelectorAll('select.form-select').forEach(select => {
                select.classList.remove('night-mode');
            });
        document.querySelectorAll('table.table').forEach(table => {
            table.classList.remove('night-mode');
        });
    }
});
