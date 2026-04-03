document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[data-prevent-double-submit="true"]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (form.dataset.submitting === 'true') {
                event.preventDefault();
                return;
            }
            form.dataset.submitting = 'true';
            form.querySelectorAll('button[type="submit"], input[type="submit"]').forEach(function (button) {
                button.disabled = true;
            });
        });
    });
});
