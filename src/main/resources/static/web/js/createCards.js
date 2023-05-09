const { createApp } = Vue;
createApp({
    data() {
        return {
            type:"",
            color:""
        };
    },
    methods: {
        createCard() {
            axios.post('/api/clients/current/cards',`type=${this.type}&color=${this.color}`)
                .then(response => {
                    Swal.fire(
                        response.data,
                        'You have created a new card!',
                        'success'
                      )
                    console.log("created card")
                    this.data = response.data
                    this.cuentas = this.data.accounts
                    console.log(this.data)
                    
                })
                .catch(err =>{
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: err.response.data,
                      })
                console.error(err);
                this.error = "failed to create card. Please try again!"    
            });    
        },
        logout() {
            axios.post('/api/logout')
            .then(response => {
                        window.location.replace('./index.html')
                })
                .catch(err => console.log(err));},
    }
    ,

}).mount('#app');