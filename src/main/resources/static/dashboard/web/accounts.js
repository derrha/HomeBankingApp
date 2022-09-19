const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            transactions: [],
            loans: [],
            dateFormat: new Intl.DateTimeFormat('es-AR', { dateStyle: 'medium', timeStyle: 'medium' }),
            offCanvas: "ml-[-100%]",
            offCanvasBoolean: false,
            accountType: "",
            inputOptions: ["Cuenta Corriente", "Caja de Ahorros"],
            totalBalance : 0,
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
                    this.totalBalance = this.totalBalance + account.balance
                    account.transactions.forEach(transf => {
                        this.transactions.push(transf)
                    })
                });
                this.loans = this.client.loans
                this.transactions.sort((a, b) => {
                    if (a.id < b.id) {
                        return 1;
                    };
                    if (a.id > b.id) {
                        return -1;
                    };
                })
            })
        },
        async addAccount() {
            const { value: accountType } = await Swal.fire({
                title: 'Selecciona el tipo de cuenta',
                input: 'radio',
                inputOptions: this.inputOptions,
                inputValidator: (value) => {
                    if (!value) {
                        return 'Por favor, selecciona algun elemento'
                    }
                }
            })
            if (accountType == 0) {
                this.accountType = "CURRENT"
            } else if (accountType == 1) {
                this.accountType = "SAVING"
            }
            axios.post('/api/clients/current/accounts', "accountType=" + this.accountType, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
                .then(response => {
                    console.log(response)
                    window.location.reload()
                })
                .catch(error => console.log(error))
        },
        logOut() {
            axios.post('/api/logout').then(response =>
                window.location.href = "/dashboard/public/index.html")
        }
    }
},
).mount('#app')
