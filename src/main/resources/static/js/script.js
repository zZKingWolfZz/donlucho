document.addEventListener('DOMContentLoaded', function () {
    const menuToggle = document.querySelector('.menu-toggle');
    const navLinks = document.querySelector('.nav-links');
    const sidebar = document.querySelector('.sidebar');

    function openMenu() {
        if (sidebar) sidebar.classList.add('active');
        if (navLinks) navLinks.classList.add('active');
        document.body.classList.add('no-scroll');
        if (menuToggle) menuToggle.classList.add('active'); // animates ☰ → ✕
    }

    function closeMenu() {
        if (sidebar) sidebar.classList.remove('active');
        if (navLinks) navLinks.classList.remove('active');
        document.body.classList.remove('no-scroll');
        if (menuToggle) menuToggle.classList.remove('active');
    }

    if (menuToggle) {
        menuToggle.addEventListener('click', function (e) {
            e.stopPropagation();
            const isOpen = navLinks ? navLinks.classList.contains('active')
                : (sidebar ? sidebar.classList.contains('active') : false);
            if (isOpen) {
                closeMenu();
            } else {
                openMenu();
            }
        });

        // Close when clicking a link inside the menu
        const linksContainer = sidebar || navLinks;
        if (linksContainer) {
            linksContainer.addEventListener('click', function (e) {
                if (e.target.tagName === 'A' || e.target.closest('a')) {
                    closeMenu();
                }
            });
        }

        // Close when clicking outside
        document.addEventListener('click', function (e) {
            const isOpen = navLinks ? navLinks.classList.contains('active')
                : (sidebar ? sidebar.classList.contains('active') : false);
            if (!isOpen) return;
            if (!menuToggle.contains(e.target) &&
                (!navLinks || !navLinks.contains(e.target)) &&
                (!sidebar || !sidebar.contains(e.target))) {
                closeMenu();
            }
        });
    }

    const chatbotButton = document.getElementById('chatbotButton');
    const chatbotContainer = document.getElementById('chatbotContainer');
    const closeChat = document.getElementById('closeChat');
    const chatForm = document.getElementById('chatForm');
    const chatInput = document.getElementById('chatInput');
    const chatMessages = document.getElementById('chatMessages');

    console.log("[Chatbot Debug] Found elements:", {
        chatbotButton: !!chatbotButton,
        chatbotContainer: !!chatbotContainer,
        closeChat: !!closeChat,
        chatForm: !!chatForm,
        chatInput: !!chatInput,
        chatMessages: !!chatMessages
    });

    if (chatbotButton && chatbotContainer && closeChat && chatForm && chatInput && chatMessages) {
        console.log("[Chatbot Debug] All elements found! Initializing handlers...");
        let isChatOpen = false;

        function toggleChat(e) {
            if (e) {
                e.preventDefault();
                e.stopPropagation();
            }
            isChatOpen = !isChatOpen;
            if (isChatOpen) {
                chatbotContainer.hidden = false;
                chatbotContainer.style.display = 'flex';
                chatbotContainer.setAttribute('aria-hidden', 'false');
                chatbotButton.setAttribute('aria-expanded', 'true');
                void chatbotContainer.offsetHeight;
                chatbotContainer.style.opacity = '1';
                chatbotContainer.style.visibility = 'visible';
                setTimeout(() => closeChat.focus(), 100);
            } else {
                chatbotContainer.style.opacity = '0';
                chatbotContainer.style.visibility = 'hidden';
                chatbotButton.setAttribute('aria-expanded', 'false');
                setTimeout(() => {
                    if (!isChatOpen) {
                        chatbotContainer.hidden = true;
                        chatbotContainer.style.display = 'none';
                        chatbotContainer.setAttribute('aria-hidden', 'true');
                        chatbotButton.focus();
                    }
                }, 300);
            }
        }

        function handleChatButtonClick(e) {
            toggleChat(e);
        }

        function handleCloseChatClick(e) {
            toggleChat(e);
        }

        function handleDocumentKeyDown(e) {
            if (e.key === 'Escape' && isChatOpen) {
                toggleChat(e);
            }
        }

        function handleDocumentClick(e) {
            if (isChatOpen && !chatbotContainer.contains(e.target) && !chatbotButton.contains(e.target)) {
                toggleChat(e);
            }
        }

        function handleChatButtonKeyDown(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                toggleChat(e);
            }
        }

        chatbotButton.addEventListener('click', handleChatButtonClick);
        closeChat.addEventListener('click', handleCloseChatClick);
        document.addEventListener('keydown', handleDocumentKeyDown);
        document.addEventListener('click', handleDocumentClick);
        chatbotButton.addEventListener('keydown', handleChatButtonKeyDown);

        function initAria() {
            chatbotButton.setAttribute('role', 'button');
            chatbotButton.setAttribute('aria-expanded', 'false');
            chatbotButton.setAttribute('aria-controls', 'chatbotContainer');
            chatbotButton.setAttribute('aria-haspopup', 'dialog');
            chatbotContainer.setAttribute('role', 'dialog');
            chatbotContainer.setAttribute('aria-modal', 'true');
            chatbotContainer.setAttribute('aria-hidden', 'true');
            chatbotContainer.hidden = true;
            chatbotContainer.style.display = 'none';
            closeChat.setAttribute('aria-label', 'Cerrar chat');
        }
        initAria();

        // Local AI Agent Chat logic
        function addMessage(text, isBot = false) {
            const messageDiv = document.createElement('div');
            messageDiv.className = `chat-message ${isBot ? 'bot' : 'user'}`;
            
            const textDiv = document.createElement('div');
            textDiv.className = 'message-text';
            textDiv.textContent = text;
            
            messageDiv.appendChild(textDiv);
            chatMessages.appendChild(messageDiv);
            chatMessages.scrollTop = chatMessages.scrollHeight;
        }

        function getBotResponse(userMsg) {
            const msg = userMsg.toLowerCase().trim();
            
            // Greetings
            if (msg.includes('hola') || msg.includes('buenos dias') || msg.includes('buenas tardes') || msg.includes('buenas noches') || msg.includes('hello') || msg.includes('hi')) {
                const greetings = [
                    '¡Hola! Es un placer saludarte. Soy el Agente IA de Don Lucho. Estoy aquí para resolver tus dudas y ayudarte con tu hospedaje. ¿En qué te puedo asistir hoy?',
                    '¡Hola! Bienvenido al portal de Don Lucho. Como tu asistente virtual, puedo brindarte información sobre nuestras habitaciones, tarifas, sedes y atractivos turísticos en Huancayo. ¿Cómo te encuentras hoy?',
                    '¡Hola! ¿Qué tal? Qué gusto que nos visites. Soy el Agente de Inteligencia Artificial de Don Lucho. ¿Te gustaría conocer nuestras habitaciones o necesitas ayuda para reservar?'
                ];
                return greetings[Math.floor(Math.random() * greetings.length)];
            }
            
            // AI Identity / Who are you
            if (msg.includes('quien eres') || msg.includes('que eres') || msg.includes('como te llamas') || msg.includes('ia') || msg.includes('inteligencia') || msg.includes('antigravity') || msg.includes('robot')) {
                return 'Soy el Agente IA de Don Lucho. Mi objetivo es ser tu guía virtual para tu estancia en Huancayo. Si deseas conectarme con una API de IA avanzada (como Gemini) para que responda sobre cualquier tema libre, avísale a mi creador y lo programará de inmediato en el backend.';
            }

            // How are you
            if (msg.includes('como estas') || msg.includes('como te va') || msg.includes('todo bien')) {
                return '¡Excelente y muy feliz de conversar contigo! Estoy listo para ayudarte con todo lo relacionado a tu estancia en el Hotel y Hostal Don Lucho. ¿Deseas hacer una reserva o consultar tarifas?';
            }

            // Rooms / pricing
            if (msg.includes('habitacion') || msg.includes('habitaciones') || msg.includes('cuarto') || msg.includes('cuartos') || msg.includes('cama') || msg.includes('camas') || msg.includes('precio') || msg.includes('tarifa') || msg.includes('costo') || msg.includes('precios') || msg.includes('tarifas') || msg.includes('costos')) {
                return 'Te cuento que en Don Lucho contamos con una variedad de habitaciones diseñadas para tu comodidad: desde habitaciones individuales (ideales para viajeros solos o de negocios), hasta amplias habitaciones matrimoniales y familiares. Todas incluyen baño privado, agua caliente las 24 horas, Smart TV con cable y conexión WiFi de alta velocidad. Los precios varían según la sede y el tipo de habitación. Puedes consultar la disponibilidad y tarifas en tiempo real haciendo clic en la sección "Reservar" o ver el catálogo en "Habitaciones".';
            }

            // Booking process
            if (msg.includes('reservar') || msg.includes('reserva') || msg.includes('reservas') || msg.includes('alojamiento') || msg.includes('hospedarme') || msg.includes('disponibilidad')) {
                return '¡Excelente! Reservar es sumamente sencillo:\n1. Dirígete a la pestaña "Reservar" en el menú superior.\n2. Selecciona la sede (como la Sede Central Huancayo) y las fechas de tu estancia.\n3. Elige tu habitación disponible favorita.\n4. Rellena tus datos y ¡listo! Tu reserva quedará confirmada al instante. ¿Te gustaría saber cómo contactarnos por si tienes un grupo grande?';
            }

            // Sede / Location
            if (msg.includes('sede') || msg.includes('sedes') || msg.includes('huancayo') || msg.includes('ubicacion') || msg.includes('donde estan') || msg.includes('direccion') || msg.includes('llegar') || msg.includes('mapa')) {
                return 'Nuestra Sede Central se encuentra en la hermosa ciudad de Huancayo (Junín, Perú), una ubicación estratégica y segura que te permitirá desplazarte con facilidad por la ciudad. Desde el hotel, estarás a pocos minutos de restaurantes tradicionales, centros comerciales y destinos turísticos imperdibles como el Parque de la Identidad Wanka, Torre Torre y Huarihuilca. ¿Quieres recomendaciones turísticas?';
            }

            // Tourism / Recommendations
            if (msg.includes('turismo') || msg.includes('viaje') || msg.includes('recomienda') || msg.includes('recomendacion') || msg.includes('visitar') || msg.includes('pasear') || msg.includes('conocer') || msg.includes('atractivos') || msg.includes('parque') || msg.includes('torre')) {
                return '¡Huancayo tiene lugares maravillosos! Te recomiendo visitar:\n- **Parque de la Identidad Wanka:** Un hermoso parque temático construido con piedra local que celebra la cultura Wanka.\n- **Torre Torre:** Impresionantes formaciones geológicas de arcilla formadas por la erosión, a solo 15 minutos del centro.\n- **Huarihuilca:** Un importante santuario arqueológico prehispánico.\n¿Te gustaría que te ayude a planificar tu itinerario o prefieres ver la información de reservas?';
            }

            // Contact / Phone
            if (msg.includes('contacto') || msg.includes('telefono') || msg.includes('celular') || msg.includes('correo') || msg.includes('email') || msg.includes('whatsapp') || msg.includes('llamar') || msg.includes('soporte') || msg.includes('recepcion') || msg.includes('atencion')) {
                return '¡Por supuesto! Puedes ponerte en contacto con nuestro equipo de recepción las 24 horas del día. Escríbenos directamente a nuestro WhatsApp oficial al **+51 987 654 321** o envíanos un correo a **reservas@hoteldonlucho.com**. ¡Estaremos encantados de ayudarte!';
            }

            // Food / Breakfast / Restaurant
            if (msg.includes('comida') || msg.includes('restaurante') || msg.includes('desayuno') || msg.includes('almuerzo') || msg.includes('cena') || msg.includes('buffet') || msg.includes('pachamanca')) {
                return 'En Don Lucho ofrecemos deliciosas opciones gastronómicas. Dependiendo de tu paquete o tarifa de reserva, puedes disfrutar de desayunos incluidos. Además, al estar ubicados de forma céntrica en Huancayo, estarás muy cerca de los mejores restaurantes para probar platos tradicionales como la Pachamanca, la Papa a la Huancaína y el Cuy colorado.';
            }

            // Help / Capabilities
            if (msg.includes('ayuda') || msg.includes('que haces') || msg.includes('funciones') || msg.includes('puedes hacer')) {
                return 'Puedo ayudarte con información detallada sobre:\n- Reservar una habitación paso a paso.\n- Tipos de habitaciones y comodidades disponibles.\n- Ubicación, mapa y contacto de nuestras sedes.\n- Destinos y atractivos turísticos en Huancayo.\nSolo escribe tu duda y con gusto te responderé.';
            }

            // Fallback
            return 'Entiendo perfectamente tu consulta. Al ser un asistente local enfocado en el Hotel Don Lucho, me especializo en responder dudas sobre reservas, tarifas, habitaciones y lugares para visitar en Huancayo. Si deseas realizar consultas generales o de otros temas libres, ¡avísale a mi programador para que me integre con la API de Gemini!';
        }

        chatForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const messageText = chatInput.value.trim();
            if (!messageText) return;

            // Add user message
            addMessage(messageText, false);
            chatInput.value = '';

            // Add typing indicator
            const typingIndicator = document.createElement('div');
            typingIndicator.className = 'chat-message bot typing';
            typingIndicator.innerHTML = '<div class="message-text"><i>Escribiendo...</i></div>';
            chatMessages.appendChild(typingIndicator);
            chatMessages.scrollTop = chatMessages.scrollHeight;

            // Call backend Gemini AI chat API
            fetch('/api/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ message: messageText })
            })
            .then(res => res.json())
            .then(data => {
                typingIndicator.remove();
                addMessage(data.reply, true);
            })
            .catch(err => {
                console.error("Chat API Error:", err);
                typingIndicator.remove();
                addMessage("Lo siento, hubo un inconveniente al conectar con mi cerebro de IA. Por favor, inténtalo de nuevo en unos momentos.", true);
            });
        });
    }
});
document.addEventListener('DOMContentLoaded', () => {
    const navItems = document.querySelectorAll('.nav-item');
    const mainContent = document.querySelector('.main-content');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
            const page = item.getAttribute('data-page');
        });
    });
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('click', () => {
        });
    });
    const serviceItems = document.querySelectorAll('.service-item');
    serviceItems.forEach(item => {
        item.addEventListener('click', () => {
        });
    });
    const searchInput = document.querySelector('.search-input');
    searchInput?.addEventListener('focus', function () {
        if (mainContent) mainContent.style.paddingBottom = '80px';
    });
    if (/iPhone|iPad|iPod/.test(navigator.userAgent)) {
        document.body.style.webkitUserSelect = 'none';
    }

    // ── Mobile accordion for dashboard tables ──
    function initMobileAccordion() {
        const cells = document.querySelectorAll('.custom-table td.mobile-name');
        cells.forEach(cell => {
            // Avoid duplicate listeners
            if (cell.dataset.accordionBound) return;
            cell.dataset.accordionBound = '1';
            cell.addEventListener('click', function (e) {
                e.stopPropagation(); // prevent document-level click handlers from interfering
                const row = this.closest('tr');
                if (!row) return;
                const isExpanded = row.classList.contains('expanded');
                // Collapse all other rows in the same table
                const table = row.closest('table');
                if (table) {
                    table.querySelectorAll('tr.expanded').forEach(r => r.classList.remove('expanded'));
                }
                if (!isExpanded) {
                    row.classList.add('expanded');
                }
            });
        });
    }
    initMobileAccordion();
    // Unified Modal Closing Logic
    window.addEventListener('click', function (event) {
        const featureModal = document.getElementById('featureModal');
        const descriptionModal = document.getElementById('descriptionModal');
        const modal = document.getElementById('featureModal'); // Alias for safety

        if (featureModal && event.target == featureModal) {
            closeFeatureModal();
        }
        if (descriptionModal && event.target == descriptionModal && typeof closeModal === 'function') {
            closeModal();
        }
        // Fallback for generic 'modal' id usage if consistent
        if (modal && event.target == modal) {
            closeFeatureModal();
        }
    });
}); // Close first DOMContentLoaded

// Carousel Navigation Logic
document.addEventListener('DOMContentLoaded', function () {
    const carousel = document.querySelector('.experience-grid');
    const prevBtn = document.querySelector('.carousel-btn.prev');
    const nextBtn = document.querySelector('.carousel-btn.next');

    if (carousel && prevBtn && nextBtn) {
        nextBtn.addEventListener('click', () => {
            const cardWidth = carousel.querySelector('div').offsetWidth;
            carousel.scrollBy({
                left: cardWidth + 16, // card width + gap
                behavior: 'smooth'
            });
        });

        prevBtn.addEventListener('click', () => {
            const cardWidth = carousel.querySelector('div').offsetWidth;
            carousel.scrollBy({
                left: -(cardWidth + 16),
                behavior: 'smooth'
            });
        });
    }
});

function showFeatureModal(title, description) {
    const modal = document.getElementById('featureModal');
    const titleEl = document.getElementById('featureModalTitle');
    const descEl = document.getElementById('featureModalDescription');
    if (modal && titleEl && descEl) {
        titleEl.textContent = title;
        descEl.textContent = description;
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeFeatureModal() {
    const modal = document.getElementById('featureModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }
}
