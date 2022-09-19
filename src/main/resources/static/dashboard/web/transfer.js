const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            loans: [],
            transferMyAccounts: false,
            transferOtherAccounts: false,
            selectedClassMyAccounts: "",
            selectedClassOtherAccounts: "",
            error: "",
            errorBoolean: false,
            amount: 100,
            description: "",
            sourceAccount: "",
            destinationAccount: "",
            destinationClient: "",
            offCanvas : "ml-[-100%]",
            offCanvasBoolean : false,
            //este array es para mostrar el max amount en el input
            sourceAccounts: [],
            startDate : "",
            endDate : "",
        }
    },
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
        filterAccount() {
            this.sourceAccounts = this.accounts.filter(account => account.number == this.sourceAccount)
        },
        getDestinationClient() {
            axios.get('/api/transactions/' + this.destinationAccount).then(response => {
                this.destinationClient = response.data
            })
        },
        transferMyAccountsSwich() {
            if (this.transferMyAccounts) {
                this.transferMyAccounts = false
                this.selectedClassMyAccounts = ""
            } else {
                this.transferMyAccounts = true;
                this.transferOtherAccounts = false;
                this.selectedClassMyAccounts = "bg-blue-600/75 text-white"
                this.selectedClassOtherAccounts = ""
            }
        },
        transferOtherAccountsSwich() {
            if (this.transferOtherAccounts) {
                this.transferOtherAccounts = false;
                this.selectedClassOtherAccounts = ""
            } else {
                this.transferOtherAccounts = true;
                this.transferMyAccounts = false;
                this.selectedClassOtherAccounts = "bg-blue-600/75 text-white"
                this.selectedClassMyAccounts = ""
            }
        },
        transfer() {
            Swal.fire({
                title: 'Estas seguro?',
                text: "estas a punto de realizar una transferencia",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Transferir'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post("/api/transactions", "amount=" + this.amount + "&description=" + this.description + "&sourceNumber=" + this.sourceAccount + "&destinationNumber=" + this.destinationAccount,
                    { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                    .then(response => {
                        Swal.fire(
                            'Listo',
                            'Transferencia realizada con éxito',
                            'success'
                        )
                    })
                    .catch(error => {
                        if (error.response.data == "Not enough amount") {
                            this.error = "Saldo insuficiente"
                            this.errorBoolean = true
                        } else if (error.response.data == "Missing data") {
                            this.error = "Faltan rellenar campos"
                            this.errorBoolean = true
                        } else if (error.response.data == "Missing destination") {
                            this.error = "Destinatario inválido"
                            this.errorBoolean = true
                        } else if (error.response.data == "This account do not belong to this client") {
                            this.error = "Esta cuenta no pertenece al cliente seleccionado"
                            this.errorBoolean = true
                        } else if (error.response.data == "Missing client") {
                            this.error = "Falta cliente"
                            this.errorBoolean = true
                        } else if (error.response.data == "Missing source account") {
                            this.error = "Falta cuenta de origen"
                            this.errorBoolean = true
                        } else if (error.response.data == "Select another account") {
                            this.error = "Selecciona otra cuenta"
                            this.errorBoolean = true
                        } else if (error.response.data == "Negative amount") {
                            this.error = ""
                            this.errorBoolean = true
                        }
                    })
                }
            })
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
            })
        },
        logOut() {
            axios.post('/api/logout').then(response =>
                window.location.href = "/dashboard/public/index.html")
        }
    }
},
).mount('#app')