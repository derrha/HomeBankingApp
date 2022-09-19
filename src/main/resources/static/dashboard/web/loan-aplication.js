const { createApp } = Vue
createApp({
    data() {
        return {
            client: {},
            accounts: [],
            transactions: [],
            loans: [],
            offCanvas: "ml-[-100%]",
            offCanvasBoolean: false,
            loanId: "",
            loan: "",
            loanAmount: 0,
            loanPayments: 0,
            accountNumber: "",
            interest : 0,
            booleanForm: false,
            moneyFormat: new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }),
            percentFormat: new Intl.NumberFormat('en-US', { style: 'percent', maximumFractionDigits: 2 }),
        }
    },
    created() {
        this.loadData()
        this.getLoans()
    },
    methods: {
        selectedLoan(loan) {
            this.loan = loan
            this.id = loan.id
            this.interest = loan.interest
            this.booleanForm = true
        },
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
        getLoans() {
            axios.get("/api/loans")
                .then(response => {
                    this.loans = response.data
                })
                .catch(error => console.log(error))
        },
        applyLoan() {
            Swal.fire({
                title: 'Solicitud de préstamo ' + this.loan.name,
                html: "<p>Estas solicitando: " + this.moneyFormat.format(this.loanAmount) + "</p><br><p>Costo por mes: " + this.moneyFormat.format((this.loanAmount * (1 + this.interest)) / this.loanPayments) + "</p><br><p>Total a pagar: " + this.moneyFormat.format(this.loanAmount*(1 + this.interest)) + "</p>",
                showCancelButton: true,
                cancelButtonText: 'Cancelar',
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Confirmar'
            }).then((result) => {
                if (result.isConfirmed) {
                    axios.post("/api/loans", { id: this.id, amount: this.loanAmount, payments: this.loanPayments, destinationNumberAccount: this.accountNumber })
                    .then(response => {
                        Swal.fire(
                            'Felicidades!',
                            'El prestamo fue aprobado.',
                            'success'
                        )
                    })
                    .catch(error => {
                        Swal.fire("Préstamo denegado")
                    })
                }
            })
        },
        logOut() {
            axios.post('/api/logout').then(response =>
                window.location.href = "/dashboard/public/index.html")
        }
    }
},
).mount('#app')
