const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            transactions: [],
            loans: [],
            cards: [],
            creditCards: [],
            debitCards: [],
            formateadorFecha: new Intl.DateTimeFormat('es-MX', { month: 'numeric', year: '2-digit' }),
            offCanvas: "ml-[-100%]",
            offCanvasBoolean: false,
            cardNumber : "",
            state : "activate"
        }
    },
    created() {

        this.loadData()
    },
    methods: {
        toggleMenu() {
            if (this.offCanvasBoolean) {
                this.offCanvasBoolean = false,
                    this.offCanvas = "ml-[-100%] "
            } else {
                this.offCanvasBoolean = true,
                    this.offCanvas = "ml-[0%]"
            }
        },
        loadData() {
            axios.get('/api/clients/current').then(response => {
                this.client = response.data
                this.accounts = response.data.accounts
                this.accounts.forEach(account => {
                    account.transactions.forEach(transf => {
                        this.transactions.push(transf)
                    })
                });
                this.loans = this.client.loans
                this.cards = this.client.cards
                this.cards.forEach(card => {
                    if (card.type == "CREDIT") {
                        this.creditCards.push(card)
                    } else {
                        this.debitCards.push(card)
                    }
                })
            })
        },
        selectedCard(card){
            if(card.active){
                this.cardNumber = card.number
                this.state = "deactivate"
                this.cardStateSwich()
            }else{
                this.cardNumber = card.number
                this.state = "activate"
                this.cardStateSwich()
            }
        },
        cardStateSwich() {
            axios.patch('/api/cards/state', "number=" + this.cardNumber + "&state=" + this.state, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    swal.fire({
                        title: '¡Tarjeta desactivada!',
                        icon: "success",
                        showConfirmButton: false,
                        color: 'black',
                        background: "white",
                    })
                    setTimeout(() => {
                        window.location.reload()
                    }, 1500)
                }).catch(response => {
                    swal.fire({
                        title: "¡Algo salió mal!",
                        text: "Intentá nuevamente.",
                        icon: "error",
                    });
                })
        },
        logOut() {
            axios.post('/api/logout').then(response =>
                window.location.href = "/dashboard/public/index.html")
        }
    }
},
).mount('#app')
