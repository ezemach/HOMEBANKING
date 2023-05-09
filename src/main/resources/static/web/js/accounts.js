const { createApp } = Vue;
const app = createApp({
    data() {
        return {
            data: [],
            cuentas:[],
            prestamos:[],
        }
    },
    created() {
        axios.get('http://localhost:8080/api/clients/current')
            .then(response => {
                this.data = response.data
                this.cuentas = this.data.accounts
                this.prestamos = this.data.loans
                console.log(this.data)
                console.log(this.cuentas)
                console.log(this.prestamos)
            })
            .catch(err => console.log(err))
    },
    methods:{
    newAccount() {
            axios.post('http://localhost:8080/api/clients/current/accounts')
            .then(response => {
               this.data = response.data
                this.cuentas = this.data.accounts
                console.log(this.data)
                console.log(this.cuentas) 
            })
            .catch(err => console.log(err))
            },    
    logout() {
            axios.post('/api/logout')
            .then(response => {
                        window.location.replace('./index.html')
                })
                .catch(err => console.log(err));},
            },
        });
        
    

app.mount('#appAccounts')