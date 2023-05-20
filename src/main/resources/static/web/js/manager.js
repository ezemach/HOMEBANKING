const { createApp } = Vue;
const app = createApp({
    data() {
        return {
            data: [],
            loans:"",
            firstName: "",
            lastName: "",
            email: "",
            loanName:"",
            loanMaxAmount:"",
            loanPayments:[],
            loanInterests:"",
        }
    },
    created() {
        axios.get('http://localhost:8080/rest/clients')
            .then(response => {
                this.data = response.data._embedded.clients
                console.log(this.data)
            })
            .catch(err => console.log(err)),
            this.loadData()
    },
    methods: {
        addClient() {
            axios({
                method: 'post',
                url: 'http://localhost:8080/rest/clients',
                data: {
                    firstName: this.firstName,
                    lastName: this.lastName,
                    email: this.email,
                }
            })
        },
        loadData(){
            axios.get('/api/loans')
            .then(response => {
                this.loans = response.data
                console.log(this.loans) 
            })
            .catch(err => console.log(err))
          },
        createLoan(){
            console.log({
                name: this.loanName,
                maxAmount: this.loanMaxAmount,
                payments: this.loanPayments
                .split(', ')
                ,
                interests: this.loanInterests,
            })
            axios.post('/api/loans/create',{
                name: this.loanName,
                maxAmount: this.loanMaxAmount,
                payments: this.loanPayments.split(',').map(payment=> parseInt(payment))
                ,
                interests: this.loanInterests,
            })
            .then(response => {
                window.location.replace('./manager.html')
            })
            .catch(err => {
                Swal.fire({
                    icon: 'error',
                    text: err.response.data,
                    confirmButtonColor: '#363333',
                }

                )

            });
        }
    }
})
app.mount('#app')