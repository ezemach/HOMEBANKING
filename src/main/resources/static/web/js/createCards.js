const { createApp } = Vue;
createApp({
    data() {
        return {
            type:"",
            color:"",
            tarjetaCredito: "",
            tarjetaDebito: "",
            cardActive:""
        };
    },
    methods: {
        createCard() {
            axios.post('/api/clients/current/cards',`type=${this.type}&color=${this.color}`)
                .then(response => {
                    
                    Swal.fire({
                        icon: 'success', text: 'Card create successful', showConfirmButton: false, timer: 2000,    
                    }
                      )
                    }
                ).then(() => window.location.href = "./cards.html")
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