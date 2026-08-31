// Client-side interactions for Employee Helpdesk System
document.addEventListener('DOMContentLoaded', () => {
    console.log("Employee Helpdesk System - DevOps App Initialized");

    // Auto-dismiss alert messages after 5 seconds
    const alert = document.querySelector('.alert-success');
    if (alert) {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s ease';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    }

    // Quick client-side filter helper for tables
    const tableSearch = document.getElementById('tableSearchInput');
    if (tableSearch) {
        tableSearch.addEventListener('keyup', function() {
            const value = this.value.toLowerCase();
            const rows = document.querySelectorAll('.custom-table tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.indexOf(value) > -1 ? '' : 'none';
            });
        });
    }
});
