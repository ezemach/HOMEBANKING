const { createApp } = Vue;
const app = createApp({
    data() {
        return {
            data: [],
            cuentas: [],
            prestamos: [],
            type:"",
            selectAccount:"",
            accountActive:""
        }
    },
    created() {
        axios.get('http://localhost:8080/api/clients/current')
            .then(response => {
                this.data = response.data
                this.prestamos = this.data.loans
                this.cuentas = this.data.accounts.filter(account => account.active)
                this.accountActive = this.data.accounts.filter(account => account.active)
                
                console.log(this.data)
                console.log(this.cuentas)
                console.log(this.prestamos)
            })
            .catch(err => console.log(err))
    },
    methods: {
        newAccount() {
            axios.post('http://localhost:8080/api/clients/current/accounts',`type=${this.type}`)
                .then(response => {
                    window.location.href = "./accounts.html"
                })
                .catch(err => console.log(err))
        },
        newAccountType(){
            this.selectAccount = true
        },
        deleteAccount(id) {
            Swal.fire({
                title: 'Are you sure you want to delete Account?',
                inputAttributes: { autocapitalize: 'off' },
                showCancelButton: true, confirmButtonText: 'Sure',
                preConfirm: () => {
                    axios.put(`/api/accounts/${id}`)
                    .then(response => Swal.fire({ icon: 'success', text: 'Account delete successful', showConfirmButton: false, timer: 2000, })
                        .then(() => window.location.href = "./accounts.html")
                        .catch(error => console.log(error)))
                    .catch(error => { Swal.fire({ icon: 'error', text: error.response.data, }) })
                }
            })
        },
        logout() {
            axios.post('/api/logout')
                .then(response => {
                    window.location.replace('./index.html')
                })
                .catch(err => console.log(err));
        },
    },
});



app.mount('#appAccounts')