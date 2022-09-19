const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            transactions : [],
            loans : [],
            cards : [],
            creditCards : [],
            debitCards : [],
            clientMail : "",
            inputCardType : "",
            inputCardColor : "",
            maxCards : false,
            offCanvas : "ml-[-100%]",
            offCanvasBoolean : false,
            formateadorFecha: new Intl.DateTimeFormat('es-MX', {month:'numeric', year:'2-digit'})
            }},
    created() {
        this.loadData()
    },
methods: {
    toggleMenu(){
        if(this.offCanvasBoolean){
            this.offCanvasBoolean = false,
            this.offCanvas = "ml-[-100%] "
        }else{
            this.offCanvasBoolean = true,
            this.offCanvas = "ml-[0%]"
        }
    },
    loadData(){
        axios.get('/api/clients/1').then(response =>{
            this.client = response.data
            this.accounts = response.data.accounts
            this.accounts.forEach(account => {
                account.transactions.forEach(transf =>{
                    this.transactions.push(transf)
                })
            });
            this.loans = this.client.loans
            this.cards = this.client.cards
            this.cards.forEach(card =>{
                if(card.type == "CREDIT"){
                    this.creditCards.push(card)
                }else{
                    this.debitCards.push(card)
                }
            })
            console.log(this.creditCards)
        })
    },
    createCard(){
        axios.post('/api/clients/current/cards', "type=" + this.inputCardType + "&color=" + this.inputCardColor , {headers:{'content-type':'application/x-www-form-urlencoded'}})
        .then(response =>
            window.location.href = "/dashboard/web/cards.html"
            ).catch(response => {
                this.maxCards = true;
            })
    },
    cancelCreateCard(){
        window.location.href = "/dashboard/web/cards.html"
    },
    logOut(){
        axios.post('/api/logout').then(response =>
            window.location.href="/dashboard/public/index.html")
    }
    }
},
).mount('#app')
