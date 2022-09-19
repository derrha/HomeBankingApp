const { createApp } = Vue
createApp({
    data() {
        return {
            clients: [],
            clientName: "",
            clientLastName: "",
            clientMail: ""
            }},
    created() {
        this.loadData()
    },
methods: {
    loadData(){
        axios.get('/clients').then(response =>{
            this.clients = response.data._embedded.clients
        })
    },
    addClient(){
        
        if(this.clientName != "" && this.clientLastName != "" && this.clientMail != ""){
            this.postClient()
        }
    },
    postClient(){
        axios.post('/clients', {
            clientName : this.clientName,
            clientLastName : this.clientLastName,
            clientMail : this.clientMail
        }).then(response =>{
            this.clients.push(response.data)
        })
    },
    deleteClient(url, event){
        event.target.parentNode.parentNode.remove()
        axios.delete(url)
        
    }

    }
},
).mount('#app')