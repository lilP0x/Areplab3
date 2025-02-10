document.getElementById("postForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Evita que la página se recargue

    const mensaje = document.getElementById("mensaje").value;

    fetch("/save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mensaje: mensaje })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById("serverResponse").innerText = "Servidor dice: " + data.message;
    })
    .catch(error => {
        console.error("Error en la petición:", error);
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const button = document.getElementById("colorButton");

    if (button) {
        button.addEventListener("click", cambiarColor);
    }
});

function cambiarColor() {
    document.body.style.backgroundColor =
        "#" + Math.floor(Math.random() * 16777215).toString(16);
}
