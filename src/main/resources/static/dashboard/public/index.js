const { createApp } = Vue
createApp({
    data() {
        return {
            clientMail : "",
            clientPassword : "",
            inputType : "password",
            error : false,
        }
    },
    created() {

    },
    methods: {
        logIn() {
            axios.post('/api/login', "clientMail=" + this.clientMail +  "&password=" + this.clientPassword, {headers: { 'content-type': 'application/x-www-form-urlencoded'} })
                .then(response => window.location.href="/dashboard/web/accounts.html")
                .catch(respone => this.error = true)
        },
        toggleInput(){
            this.inputType = this.inputType === "password" ? "text" : "password"
        },
    }
},
).mount('#app')


