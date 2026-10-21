const backendUrl = 'http://localhost:8080'; 
const frontendUrl = 'http://localhost:3000'; 

// sample fetch
fetch(`${backendUrl}/api/xyz`)
    .then(response => {
        if (!response.ok) { throw new Error("API-hiba: " + response.status); }
        return response.json();
    })
    .then(data => console.log(data))
    .catch(error => console.error(error));
    //.then(response => response.json())