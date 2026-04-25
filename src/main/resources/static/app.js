const state = {
    view: "pokemons",
    pokemons: [],
    allPokemons: [],
    trainers: [],
    rentals: [],
    allRentals: [],
    pokemonFilter: {},
    rentalStatus: "",
    dialog: null
};

const API_BASE_URL = resolveApiBaseUrl();

const statusLabels = {
    AVAILABLE: "Dostepny",
    RENTED: "Wypozyczony",
    INJURED: "Kontuzjowany",
    ACTIVE: "Aktywne",
    RETURNED: "Zwrocone",
    CANCELLED: "Anulowane"
};

const els = {
    tabs: document.querySelectorAll(".tab"),
    views: document.querySelectorAll(".view"),
    title: document.querySelector("#view-title"),
    primaryAction: document.querySelector("#primary-action"),
    refresh: document.querySelector("#refresh-button"),
    alert: document.querySelector("#alert"),
    pokemonRows: document.querySelector("#pokemon-rows"),
    trainerRows: document.querySelector("#trainer-rows"),
    rentalRows: document.querySelector("#rental-rows"),
    pokemonFilters: document.querySelector("#pokemon-filters"),
    clearPokemonFilters: document.querySelector("#clear-pokemon-filters"),
    rentalStatusFilter: document.querySelector("#rental-status-filter"),
    dialog: document.querySelector("#entity-dialog"),
    entityForm: document.querySelector("#entity-form"),
    dialogTitle: document.querySelector("#dialog-title"),
    dialogFields: document.querySelector("#dialog-fields"),
    closeDialog: document.querySelector("#close-dialog"),
    cancelDialog: document.querySelector("#cancel-dialog"),
    statPokemons: document.querySelector("#stat-pokemons"),
    statAvailable: document.querySelector("#stat-available"),
    statActive: document.querySelector("#stat-active")
};

const api = {
    async request(path, options = {}) {
        const response = await fetch(`${API_BASE_URL}${path}`, {
            headers: {
                "Content-Type": "application/json",
                ...options.headers
            },
            ...options
        });

        if (response.status === 204) {
            return null;
        }

        const contentType = response.headers.get("content-type") || "";
        const payload = contentType.includes("application/json") ? await response.json() : await response.text();

        if (!response.ok) {
            throw normalizeError(payload, response.status);
        }

        return payload;
    },
    get(path) {
        return this.request(path);
    },
    post(path, body) {
        return this.request(path, { method: "POST", body: JSON.stringify(body) });
    },
    put(path, body) {
        return this.request(path, { method: "PUT", body: JSON.stringify(body) });
    },
    patch(path, body) {
        const options = { method: "PATCH" };
        if (body !== undefined) {
            options.body = JSON.stringify(body);
        }
        return this.request(path, options);
    },
    delete(path) {
        return this.request(path, { method: "DELETE" });
    }
};

function resolveApiBaseUrl() {
    const configured = window.localStorage.getItem("pokemonRentalApiUrl");
    if (configured) {
        return configured.replace(/\/$/, "");
    }

    const isLocalHost = ["localhost", "127.0.0.1", ""].includes(window.location.hostname);
    const isSpringBootPort = window.location.port === "8080";

    if (window.location.protocol === "file:" || (isLocalHost && !isSpringBootPort)) {
        return "http://localhost:8080";
    }

    return "";
}

function normalizeError(payload, status) {
    if (payload && typeof payload === "object") {
        return {
            status,
            message: payload.message || "Nie udalo sie wykonac operacji",
            errors: payload.errors || {}
        };
    }

    return {
        status,
        message: payload || "Nie udalo sie wykonac operacji",
        errors: {}
    };
}

function showAlert(message, kind = "error") {
    els.alert.textContent = message;
    els.alert.hidden = false;
    els.alert.dataset.kind = kind;
}

function clearAlert() {
    els.alert.hidden = true;
    els.alert.textContent = "";
}

function buildQuery(params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value !== null && value !== undefined && value !== "") {
            query.set(key, value);
        }
    });
    const text = query.toString();
    return text ? `?${text}` : "";
}

async function loadData() {
    clearAlert();
    try {
        const [pokemons, allPokemons, trainers, rentals, allRentals] = await Promise.all([
            api.get(`/api/pokemons${buildQuery(state.pokemonFilter)}`),
            api.get("/api/pokemons"),
            api.get("/api/trainers"),
            api.get(`/api/rentals${buildQuery({ status: state.rentalStatus })}`),
            api.get("/api/rentals")
        ]);

        state.pokemons = pokemons;
        state.allPokemons = allPokemons;
        state.trainers = trainers;
        state.rentals = rentals;
        state.allRentals = allRentals;
        render();
    } catch (error) {
        showAlert(`${error.message}. Sprawdz, czy backend dziala pod adresem ${API_BASE_URL || window.location.origin}.`);
    }
}

function render() {
    renderStats();
    renderPokemons();
    renderTrainers();
    renderRentals();
    updateViewChrome();
}

function renderStats() {
    els.statPokemons.textContent = state.allPokemons.length;
    els.statAvailable.textContent = state.allPokemons.filter((pokemon) => pokemon.status === "AVAILABLE").length;
    els.statActive.textContent = state.allRentals.filter((rental) => rental.status === "ACTIVE").length;
}

function renderPokemons() {
    els.pokemonRows.innerHTML = "";
    if (!state.pokemons.length) {
        els.pokemonRows.append(emptyRow(7, "Brak Pokemonow dla wybranych filtrow."));
        return;
    }

    state.pokemons.forEach((pokemon) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${pokemon.id}</td>
            <td><strong>${escapeHtml(pokemon.name)}</strong></td>
            <td>${escapeHtml(pokemon.type)}</td>
            <td>${pokemon.level}</td>
            <td>${pokemon.hp}</td>
            <td>${badge(pokemon.status)}</td>
            <td></td>
        `;
        const actions = row.lastElementChild;
        actions.append(actionGroup([
            actionButton("Edytuj", "ghost-button", () => openPokemonDialog(pokemon)),
            pokemon.status === "INJURED" ? actionButton("Ulecz", "secondary-button", () => healPokemon(pokemon.id)) : null,
            actionButton("Usun", "danger-button", () => deletePokemon(pokemon))
        ]));
        els.pokemonRows.append(row);
    });
}

function renderTrainers() {
    els.trainerRows.innerHTML = "";
    if (!state.trainers.length) {
        els.trainerRows.append(emptyRow(5, "Brak trenerow."));
        return;
    }

    state.trainers.forEach((trainer) => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${trainer.id}</td>
            <td>${escapeHtml(trainer.firstName)}</td>
            <td>${escapeHtml(trainer.lastName)}</td>
            <td>${escapeHtml(trainer.email)}</td>
            <td></td>
        `;
        const actions = row.lastElementChild;
        actions.append(actionGroup([
            actionButton("Edytuj", "ghost-button", () => openTrainerDialog(trainer)),
            actionButton("Usun", "danger-button", () => deleteTrainer(trainer))
        ]));
        els.trainerRows.append(row);
    });
}

function renderRentals() {
    els.rentalRows.innerHTML = "";
    if (!state.rentals.length) {
        els.rentalRows.append(emptyRow(7, "Brak wypozyczen."));
        return;
    }

    state.rentals.forEach((rental) => {
        const pokemon = findPokemon(rental.pokemonId);
        const trainer = findTrainer(rental.trainerId);
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${rental.id}</td>
            <td>${pokemon ? escapeHtml(pokemon.name) : `#${rental.pokemonId}`}</td>
            <td>${trainer ? escapeHtml(`${trainer.firstName} ${trainer.lastName}`) : `#${rental.trainerId}`}</td>
            <td>${formatDate(rental.rentedAt)}</td>
            <td>${formatDate(rental.returnedAt)}</td>
            <td>${badge(rental.status)}</td>
            <td></td>
        `;
        const actions = row.lastElementChild;
        const availableActions = rental.status === "ACTIVE"
            ? [
                actionButton("Zwroc", "secondary-button", () => openReturnDialog(rental)),
                actionButton("Anuluj", "danger-button", () => cancelRental(rental))
            ]
            : [document.createTextNode("Zakonczone")];
        actions.append(actionGroup(availableActions));
        els.rentalRows.append(row);
    });
}

function updateViewChrome() {
    const labels = {
        pokemons: ["Pokemony", "Dodaj Pokemona"],
        trainers: ["Trenerzy", "Dodaj trenera"],
        rentals: ["Wypozyczenia", "Nowe wypozyczenie"]
    };
    const [title, action] = labels[state.view];
    els.title.textContent = title;
    els.primaryAction.textContent = action;

    els.tabs.forEach((tab) => tab.classList.toggle("active", tab.dataset.view === state.view));
    els.views.forEach((view) => view.classList.toggle("active", view.id === `view-${state.view}`));
}

function emptyRow(colspan, text) {
    const row = document.createElement("tr");
    row.innerHTML = `<td class="empty-row" colspan="${colspan}">${text}</td>`;
    return row;
}

function badge(status) {
    return `<span class="badge ${status.toLowerCase()}">${statusLabels[status] || status}</span>`;
}

function actionGroup(items) {
    const group = document.createElement("div");
    group.className = "row-actions";
    items.filter(Boolean).forEach((item) => group.append(item));
    return group;
}

function actionButton(label, className, onClick) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = className;
    button.textContent = label;
    button.addEventListener("click", onClick);
    return button;
}

function openPokemonDialog(pokemon = null) {
    openDialog({
        title: pokemon ? "Edytuj Pokemona" : "Dodaj Pokemona",
        submitLabel: pokemon ? "Zapisz zmiany" : "Dodaj",
        fields: [
            field("name", "Nazwa", "text", pokemon?.name || "", { required: true }),
            field("type", "Typ", "text", pokemon?.type || "", { required: true }),
            field("level", "Level", "number", pokemon?.level || 1, { min: 1, max: 100, required: true }),
            field("hp", "HP", "number", pokemon?.hp || 1, { min: 1, required: true })
        ],
        onSubmit: async (values) => {
            const body = {
                name: values.name,
                type: values.type,
                level: Number(values.level),
                hp: Number(values.hp)
            };
            if (pokemon) {
                await api.put(`/api/pokemons/${pokemon.id}`, body);
            } else {
                await api.post("/api/pokemons", body);
            }
        }
    });
}

function openTrainerDialog(trainer = null) {
    openDialog({
        title: trainer ? "Edytuj trenera" : "Dodaj trenera",
        submitLabel: trainer ? "Zapisz zmiany" : "Dodaj",
        fields: [
            field("firstName", "Imie", "text", trainer?.firstName || "", { required: true }),
            field("lastName", "Nazwisko", "text", trainer?.lastName || "", { required: true }),
            field("email", "Email", "email", trainer?.email || "", { required: true })
        ],
        onSubmit: async (values) => {
            if (trainer) {
                await api.put(`/api/trainers/${trainer.id}`, values);
            } else {
                await api.post("/api/trainers", values);
            }
        }
    });
}

function openRentalDialog() {
    const availablePokemons = state.allPokemons.filter((pokemon) => pokemon.status === "AVAILABLE");
    openDialog({
        title: "Nowe wypozyczenie",
        submitLabel: "Wypozycz",
        fields: [
            selectField("pokemonId", "Pokemon", availablePokemons.map((pokemon) => ({
                value: pokemon.id,
                label: `${pokemon.name} (${pokemon.type}, lvl ${pokemon.level})`
            }))),
            selectField("trainerId", "Trener", state.trainers.map((trainer) => ({
                value: trainer.id,
                label: `${trainer.firstName} ${trainer.lastName}`
            })))
        ],
        onSubmit: async (values) => {
            await api.post("/api/rentals", {
                pokemonId: Number(values.pokemonId),
                trainerId: Number(values.trainerId)
            });
        }
    });
}

function openReturnDialog(rental) {
    const pokemon = findPokemon(rental.pokemonId);
    openDialog({
        title: `Zwrot: ${pokemon ? pokemon.name : `#${rental.pokemonId}`}`,
        submitLabel: "Przyjmij zwrot",
        fields: [
            selectField("injured", "Czy Pokemon wrocil kontuzjowany?", [
                { value: "false", label: "Nie, jest gotowy do kolejnej walki" },
                { value: "true", label: "Tak, wymaga leczenia" }
            ])
        ],
        onSubmit: async (values) => {
            await api.patch(`/api/rentals/${rental.id}/return`, {
                injured: values.injured === "true"
            });
        }
    });
}

function field(name, label, type, value, attrs = {}) {
    return { kind: "input", name, label, type, value, attrs };
}

function selectField(name, label, options) {
    return { kind: "select", name, label, options };
}

function openDialog(config) {
    state.dialog = config;
    els.dialogTitle.textContent = config.title;
    els.dialogFields.innerHTML = "";
    document.querySelector("#save-dialog").textContent = config.submitLabel;

    config.fields.forEach((item) => {
        const label = document.createElement("label");
        label.textContent = item.label;

        if (item.kind === "select") {
            const select = document.createElement("select");
            select.name = item.name;
            select.required = true;
            if (!item.options.length) {
                const option = document.createElement("option");
                option.value = "";
                option.textContent = "Brak dostepnych opcji";
                select.append(option);
                select.disabled = true;
            } else {
                item.options.forEach((entry) => {
                    const option = document.createElement("option");
                    option.value = entry.value;
                    option.textContent = entry.label;
                    select.append(option);
                });
            }
            label.append(select);
        } else {
            const input = document.createElement("input");
            input.name = item.name;
            input.type = item.type;
            input.value = item.value;
            Object.entries(item.attrs).forEach(([key, value]) => {
                input[key] = value;
            });
            label.append(input);
        }

        const error = document.createElement("p");
        error.className = "field-error";
        error.dataset.errorFor = item.name;
        label.append(error);
        els.dialogFields.append(label);
    });

    clearDialogErrors();
    els.dialog.showModal();
}

async function submitDialog(event) {
    event.preventDefault();
    if (!state.dialog) {
        return;
    }

    clearDialogErrors();
    const values = Object.fromEntries(new FormData(els.entityForm).entries());

    try {
        await state.dialog.onSubmit(values);
        closeDialog();
        await loadData();
    } catch (error) {
        applyDialogErrors(error);
    }
}

function applyDialogErrors(error) {
    const entries = Object.entries(error.errors || {});
    entries.forEach(([fieldName, message]) => {
        const target = els.dialogFields.querySelector(`[data-error-for="${fieldName}"]`);
        if (target) {
            target.textContent = message;
        }
    });

    if (!entries.length) {
        showAlert(error.message);
        closeDialog();
    }
}

function clearDialogErrors() {
    els.dialogFields.querySelectorAll(".field-error").forEach((item) => {
        item.textContent = "";
    });
}

function closeDialog() {
    state.dialog = null;
    els.dialog.close();
}

async function healPokemon(id) {
    await runAction(() => api.patch(`/api/pokemons/${id}/heal`));
}

async function deletePokemon(pokemon) {
    if (!confirm(`Usunac Pokemona ${pokemon.name}?`)) {
        return;
    }
    await runAction(() => api.delete(`/api/pokemons/${pokemon.id}`));
}

async function deleteTrainer(trainer) {
    if (!confirm(`Usunac trenera ${trainer.firstName} ${trainer.lastName}?`)) {
        return;
    }
    await runAction(() => api.delete(`/api/trainers/${trainer.id}`));
}

async function cancelRental(rental) {
    if (!confirm(`Anulowac wypozyczenie #${rental.id}?`)) {
        return;
    }
    await runAction(() => api.patch(`/api/rentals/${rental.id}/cancel`));
}

async function runAction(action) {
    clearAlert();
    try {
        await action();
        await loadData();
    } catch (error) {
        showAlert(error.message);
    }
}

function findPokemon(id) {
    return state.allPokemons.find((pokemon) => pokemon.id === id);
}

function findTrainer(id) {
    return state.trainers.find((trainer) => trainer.id === id);
}

function formatDate(value) {
    if (!value) {
        return "-";
    }
    return new Intl.DateTimeFormat("pl-PL", {
        dateStyle: "short",
        timeStyle: "short"
    }).format(new Date(value));
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

els.tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
        state.view = tab.dataset.view;
        clearAlert();
        updateViewChrome();
    });
});

els.primaryAction.addEventListener("click", () => {
    if (state.view === "pokemons") {
        openPokemonDialog();
    } else if (state.view === "trainers") {
        openTrainerDialog();
    } else {
        openRentalDialog();
    }
});

els.refresh.addEventListener("click", loadData);
els.entityForm.addEventListener("submit", submitDialog);
els.closeDialog.addEventListener("click", closeDialog);
els.cancelDialog.addEventListener("click", closeDialog);

els.pokemonFilters.addEventListener("submit", (event) => {
    event.preventDefault();
    state.pokemonFilter = Object.fromEntries(new FormData(els.pokemonFilters).entries());
    loadData();
});

els.clearPokemonFilters.addEventListener("click", () => {
    els.pokemonFilters.reset();
    state.pokemonFilter = {};
    loadData();
});

els.rentalStatusFilter.addEventListener("change", () => {
    state.rentalStatus = els.rentalStatusFilter.value;
    loadData();
});

loadData();
