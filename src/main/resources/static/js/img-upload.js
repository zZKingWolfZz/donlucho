/**
 * img-upload.js
 * Handles the dual-mode image picker (URL | File upload) used in
 * agregar_habitacion.html and editar_habitacion.html.
 *
 * Each form instance has a unique `suffix` (e.g. "agregar" or "editar")
 * that is appended to all element IDs to avoid collisions.
 */

/**
 * Switch between the URL tab and the File upload tab.
 * @param {string} mode   - "url" or "file"
 * @param {string} suffix - form instance identifier
 */
function switchImgTab(mode, suffix) {
    const urlTab   = document.getElementById('tab-url-' + suffix);
    const fileTab  = document.getElementById('tab-file-' + suffix);
    const urlPanel = document.getElementById('panel-url-' + suffix);
    const filePanel= document.getElementById('panel-file-' + suffix);
    const sourceInput = document.getElementById('imagen_source_' + suffix);

    if (mode === 'url') {
        urlTab.classList.add('active');
        fileTab.classList.remove('active');
        urlPanel.style.display = '';
        filePanel.style.display = 'none';
        sourceInput.value = 'url';

        // Show URL-based preview if something is typed
        const urlInput = document.getElementById('imagen_url_input_' + suffix);
        if (urlInput && urlInput.value.trim()) {
            previewFromUrl(urlInput.value.trim(), 'img-preview-' + suffix);
        }
    } else {
        fileTab.classList.add('active');
        urlTab.classList.remove('active');
        filePanel.style.display = '';
        urlPanel.style.display = 'none';
        sourceInput.value = 'file';
    }
}

/**
 * Show a preview when the user types/pastes a URL.
 * @param {string} url       - Image URL to preview
 * @param {string} previewId - ID of the preview wrapper div
 */
function previewFromUrl(url, previewId) {
    const wrapper = document.getElementById(previewId);
    if (!wrapper) return;

    const trimmed = url.trim();
    if (!trimmed) {
        wrapper.style.display = 'none';
        return;
    }

    // Derive the suffix from the previewId (e.g. "img-preview-agregar" → "agregar")
    const suffix = previewId.replace('img-preview-', '');
    const img = document.getElementById('img-preview-img-' + suffix);
    if (img) {
        img.src = trimmed;
        img.onerror = () => { wrapper.style.display = 'none'; };
        img.onload  = () => { wrapper.style.display = 'flex'; };
    }
}

/**
 * Show a preview when the user selects a local file.
 * @param {HTMLInputElement} input    - The <input type="file"> element
 * @param {string}           previewId - ID of the preview wrapper div
 * @param {string}           dropZoneId - ID of the drop zone div (to update text)
 */
function previewFromFile(input, previewId, dropZoneId) {
    const file = input.files && input.files[0];
    if (!file) return;

    // 5 MB guard
    if (file.size > 5 * 1024 * 1024) {
        alert('El archivo es demasiado grande. Máximo permitido: 5 MB.');
        input.value = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = function (e) {
        const suffix = previewId.replace('img-preview-', '');
        const img    = document.getElementById('img-preview-img-' + suffix);
        const wrapper= document.getElementById(previewId);
        const dz     = document.getElementById(dropZoneId);

        if (img) img.src = e.target.result;
        if (wrapper) wrapper.style.display = 'flex';

        // Update drop zone text to show file name
        if (dz) {
            dz.querySelector('p').innerHTML =
                '<i class="fas fa-check-circle" style="color:#2ecc71;margin-right:6px;"></i>' +
                '<strong>' + file.name + '</strong>';
        }
    };
    reader.readAsDataURL(file);
}

/**
 * Clear the current image preview and reset inputs for the given form instance.
 * @param {string} suffix - form instance identifier
 */
function clearPreview(suffix) {
    const wrapper   = document.getElementById('img-preview-' + suffix);
    const img       = document.getElementById('img-preview-img-' + suffix);
    const urlInput  = document.getElementById('imagen_url_input_' + suffix);
    const fileInput = document.getElementById('imagen_file_' + suffix);
    const dz        = document.getElementById('drop-zone-' + suffix);

    if (wrapper) wrapper.style.display = 'none';
    if (img)     img.src = '';
    if (urlInput)  urlInput.value = '';
    if (fileInput) {
        fileInput.value = '';
    }
    // Reset drop zone text
    if (dz) {
        dz.querySelector('p').innerHTML =
            'Arrastra una imagen aquí o <strong>haz clic para seleccionar</strong>';
    }
}

/**
 * Handle a file dragged onto the drop zone.
 * @param {DragEvent} event  - The drop event
 * @param {string}    suffix - form instance identifier
 */
function handleDrop(event, suffix) {
    event.preventDefault();
    const dz = document.getElementById('drop-zone-' + suffix);
    if (dz) dz.classList.remove('drag-over');

    const file = event.dataTransfer.files && event.dataTransfer.files[0];
    if (!file || !file.type.startsWith('image/')) {
        alert('Por favor, arrastra solo archivos de imagen (JPG, PNG, WEBP).');
        return;
    }

    // Put the file into the hidden <input type="file"> so it submits with the form
    const input = document.getElementById('imagen_file_' + suffix);
    if (input) {
        const dt = new DataTransfer();
        dt.items.add(file);
        input.files = dt.files;
        previewFromFile(input, 'img-preview-' + suffix, 'drop-zone-' + suffix);
    }
}
