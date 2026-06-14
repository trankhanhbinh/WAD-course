// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded and ready!');
    
    // Select elements
    const header = document.querySelector('.site-header h1');
    console.log('Header text:', header.textContent);
    
    // Modify element
    const changeButton = document.querySelector('#change-title');
    if (changeButton) {
        changeButton.addEventListener('click', function() {
            header.textContent = 'Welcome to Course Catalog!';
            header.classList.add('highlight');
        });
    }
    
    // Add element
    const addButton = document.querySelector('#add-message');
    if (addButton) {
        addButton.addEventListener('click', function() {
            const message = document.createElement('p');
            message.textContent = 'This message was added with JavaScript!';
            message.classList.add('alert', 'alert-success');
            
            const container = document.querySelector('.site-main .container');
            container.appendChild(message);
        });
    }
});
