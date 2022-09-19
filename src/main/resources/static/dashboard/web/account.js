const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            transactions : [],
            dateFormat : new Intl.DateTimeFormat('es-AR', { dateStyle: 'medium', timeStyle: 'medium' }),
            offCanvas : "ml-[-100%]",
            offCanvasBoolean : false,
            }},
    created() {
        const queryString = location.search
        const params = new URLSearchParams(queryString)
        const id = params.get("id")
        this.loadData(id)
        
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
    loadData(id){
        axios.get('/api/clients/current' ).then(response =>{
            this.client = response.data
            this.accounts = this.client.accounts.find(account => account.id == id)
            this.transactions = this.accounts.transactions
            console.log(this.accounts)
        })
    }
    }
},
).mount('#app')


