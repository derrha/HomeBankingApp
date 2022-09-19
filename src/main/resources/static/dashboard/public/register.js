const { createApp } = Vue
createApp({
    data() {
        return {
            firstName : "",
            lastName : "",
            email : "",
            password : "",
            confirmPassword: ""
        }
    },
    created() {

    },
    methods: {
        logIn() {
            axios.post('/api/login', "clientMail=" + this.email +  "&password=" + this.password, {headers: { 'content-type': 'application/x-www-form-urlencoded'} })
                .then(response => window.location.href="/dashboard/web/accounts.html")
        },
        register() {
            if(this.confirmPassword == this.password){
                axios.post('/api/clients',"clientName=" + this.firstName + "&clientLastName=" + this.lastName + "&clientMail=" + this.email + "&clientPassword=" + this.password ,{headers:{'content-type':'application/x-www-form-urlencoded'}})
                .then(response => this.logIn())
            }
        }
    }
},
).mount('#app')
