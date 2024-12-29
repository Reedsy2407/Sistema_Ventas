// Forzar recarga para páginas protegidas cuando se utiliza el botón de retroceso
if (performance.navigation.type === 2) { // 2 = back/forward
    window.location.reload();
}
