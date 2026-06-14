/**
 * Fetch courses from JSON file
 * @returns {Promise<Array>} Array of course objects
 */
async function fetchCourses() {
    try {
        const response = await fetch('./data/courses.json');
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const courses = await response.json();
        console.log('Loaded courses:', courses);
        return courses;
        
    } catch (error) {
        console.error('Error fetching courses:', error);
        throw error;
    }
}

/**
 * Create HTML for a single course card
 * @param {Object} course - Course object
 * @returns {HTMLElement} Course card element
 */
function createCourseCard(course) {
    // Create card container
    const card = document.createElement('div');
    card.classList.add('card', 'course-card');
    card.setAttribute('data-category', course.category);
    card.setAttribute('data-code', course.code);
    
    // Create card content
    card.innerHTML = `
        <h3 class="card__title">${course.code} - ${course.title}</h3>
        <div class="card__meta">
            <span><strong>Instructor:</strong> ${course.instructor}</span> | 
            <span><strong>Schedule:</strong> ${course.schedule}</span> | 
            <span><strong>Credits:</strong> ${course.credits}</span>
        </div>
        <div class="card__content">
            <p>${course.description}</p>
        </div>
        <div class="card__footer">
            <span class="seats ${course.seats < 10 ? 'seats--low' : ''}">
                ${course.seats} seats available
            </span>
            <button class="button button--primary" data-course="${course.code}">
                View Details
            </button>
        </div>
    `;
    
    return card;
}

/**
 * Display courses in the DOM
 * @param {Array} courses - Array of course objects
 */
function displayCourses(courses) {
    const container = document.querySelector('#course-list');
    
    if (!container) {
        console.error('Course list container not found');
        return;
    }
    
    // Clear existing content
    container.innerHTML = '';
    
    if (courses.length === 0) {
        container.innerHTML = '<p class="empty-state">No courses found</p>';
        return;
    }
    
    // Create and append cards
    courses.forEach(course => {
        const card = createCourseCard(course);
        container.appendChild(card);
    });
    
    console.log(`Displayed ${courses.length} courses`);
}


// Auto-initialize when DOM is ready
document.addEventListener('DOMContentLoaded', initCoursePage);

// Store all courses globally for filtering
let allCourses = [];

/**
 * Filter courses based on search term
 * @param {string} searchTerm - Search query
 * @returns {Array} Filtered courses
 */
function filterCourses(searchTerm) {
    if (!searchTerm || searchTerm.trim() === '') {
        return allCourses;
    }
    
    const search = searchTerm.toLowerCase().trim();
    
    return allCourses.filter(course => {
        return course.code.toLowerCase().includes(search) ||
               course.title.toLowerCase().includes(search) ||
               course.instructor.toLowerCase().includes(search) ||
               course.description.toLowerCase().includes(search);
    });
}

/**
 * Handle search input
 * @param {Event} event - Input event
 */
function handleSearch(event) {
    const searchTerm = event.target.value;
    const filtered = filterCourses(searchTerm);
    displayCourses(filtered);
    
    // Update results count
    const resultsCount = document.querySelector('#results-count');
    if (resultsCount) {
        resultsCount.textContent = `${filtered.length} course(s) found`;
    }
}

/**
 * Show course details in modal
 * @param {string} courseCode - Course code to display
 */
function showCourseDetails(courseCode) {
    const course = allCourses.find(c => c.code === courseCode);
    
    if (!course) {
        console.error(`Course ${courseCode} not found`);
        return;
    }
    
    const modal = document.querySelector('#course-modal');
    const modalBody = document.querySelector('#modal-body');
    
    // Create modal content
    modalBody.innerHTML = `
        <h2>${course.code} - ${course.title}</h2>
        <div class="course-details">
            <p><strong>Instructor:</strong> ${course.instructor}</p>
            <p><strong>Schedule:</strong> ${course.schedule}</p>
            <p><strong>Credits:</strong> ${course.credits}</p>
            <p><strong>Available Seats:</strong> ${course.seats}</p>
            
            <h3>Description</h3>
            <p>${course.description}</p>
            
            <h3>Prerequisites</h3>
            <ul>
                ${course.prerequisites.map(pre => `<li>${pre}</li>`).join('')}
            </ul>
            
            <button class="button button--primary" onclick="enrollInCourse('${course.code}')">
                Enroll Now
            </button>
        </div>
    `;
    
    // Show modal
    modal.style.display = 'block';
    document.body.style.overflow = 'hidden'; // Prevent background scrolling
}

/**
 * Hide modal
 */
function hideModal() {
    const modal = document.querySelector('#course-modal');
    modal.style.display = 'none';
    document.body.style.overflow = ''; // Restore scrolling
}

/**
 * Set up modal close handlers
 */
function setupModal() {
    const modal = document.querySelector('#course-modal');
    const closeBtn = document.querySelector('.modal__close');
    const overlay = document.querySelector('.modal__overlay');
    
    // Close on X button
    if (closeBtn) {
        closeBtn.addEventListener('click', hideModal);
    }
    
    // Close on overlay click
    if (overlay) {
        overlay.addEventListener('click', hideModal);
    }
    
    // Close on Escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && modal.style.display === 'block') {
            hideModal();
        }
    });
}

/**
 * Handle "View Details" button clicks using event delegation
 */
function setupCourseCardListeners() {
    const courseList = document.querySelector('#course-list');
    
    if (!courseList) return;
    
    courseList.addEventListener('click', (event) => {
        // Check if clicked element is a "View Details" button
        const button = event.target.closest('button[data-course]');
        if (button) {
            const courseCode = button.getAttribute('data-course');
            showCourseDetails(courseCode);
        }
    });
}

// Update initCoursePage
async function initCoursePage() {
    const container = document.querySelector('#course-list');
    
    if (!container) {
        return;
    }
    
    container.innerHTML = '<div class="loading">Loading courses...</div>';
    
    try {
        allCourses = await fetchCourses();
        displayCourses(allCourses);
        
        // Set up all event listeners
        const searchInput = document.querySelector('#course-search');
        if (searchInput) {
            searchInput.addEventListener('input', handleSearch);
        }
        
        setupCategoryFilters();
        setupModal();
        setupCourseCardListeners();
        
    } catch (error) {
        container.innerHTML = `
            <div class="error">
                <p>Failed to load courses: ${error.message}</p>
                <button class="button" onclick="initCoursePage()">Retry</button>
            </div>
        `;
    }
}

function setupCategoryFilters() {
    const filterButtons = document.querySelectorAll('.filter-btn');

    if (!filterButtons.length) return;

    filterButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));

            // Add active class to clicked button
            button.classList.add('active');

            const category = button.getAttribute('data-category');

            let filteredCourses;

            if (category === 'all') {
                filteredCourses = allCourses;
            } else {
                filteredCourses = allCourses.filter(course => 
                    course.category === category
                );
            }

            displayCourses(filteredCourses);
        });
    });
}