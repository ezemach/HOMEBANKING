const { createApp } = Vue;
createApp({
    data() {
        return {
            email:"",
            password:"",
            firstName:"",
            lastName:""
        }
    },
    methods: {
        signUp() {
            axios.post('/api/clients',`firstName=${this.firstName}&lastName=${this.lastName}&email=${this.email}&password=${this.password}`, {
                    headers:{'content-type':'application/x-www-form-urlencoded'}
                    
                })
                .then(response => {
                    
                    axios.post('/api/login',`email=${this.email}&password=${this.password}`, {
                    headers:{'content-type':'application/x-www-form-urlencoded'}})
                    .then(response => {
                        console.log(data.response)
                        window.location.replace('./accounts.html')
                })})
                .catch(err =>{
                console.error(err);
                this.error = "failed to register. Please try again!"    
            });    
        },
        newAccount() {
            if(this.signUp()){
            axios.post('http://localhost:8080/api/clients/current/accounts')
            .then(response => {
                this.data = response.data
                this.cuentas = this.data.accounts
                console.log(this.data)
                console.log(this.cuentas)
            })
            .catch(err => console.log(err))
            }},
    }

})
.mount('#app')