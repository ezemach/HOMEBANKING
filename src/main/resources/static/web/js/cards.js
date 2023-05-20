const { createApp } = Vue;

const app = createApp({
    data() {
        return {
            data: [],
            cuentas: [],
            prestamos: [],
            tarjetas: "",
            tarjetaCredito: "",
            tarjetaDebito: "",
            cardActive: "",
            type: '',
            color: ''
        };
    },
    created() {
        axios.post('http://localhost:8080/api/clients/current')
            .then(response => {
                this.data = response.data
                console.log(this.data)

                this.tarjetas = this.data.cards
                this.tarjetaDebito = this.data.cards.filter(card => card.type === "DEBIT" && card.active)
                this.tarjetaCredito = this.data.cards.filter(card => card.type === "CREDIT" && card.active)
                this.cardActive = this.data.cards.filter(card => card.active)
                console.log(this.tarjetaCredito);
                console.log(this.tarjetaDebito);
                console.log(this.data);

            })
            .catch(err => console.log(err))
    },
    methods: {
        deleteCard(id) { Swal.fire({ title: 'Are you sure you want to delete card?', 
        inputAttributes: { autocapitalize: 'off' }, 
        showCancelButton: true, confirmButtonText: 'Sure', 
        preConfirm: () => { axios.post(`/api/cards/${id}`)
        .then(response => Swal.fire({ icon: 'success', text: 'card deletion successful', showConfirmButton: false, timer: 2000, })
        .then(() => window.location.href = "./cards.html")
        .catch(error => console.log(error)))
        .catch(error => { Swal.fire({ icon: 'error', text: error.response.data, }) }) } }) },
        // deleteCard(id) {
        //     axios.put(`/api/cards/${id}`)
        //         .then(
        //             response => {

        //                 // window.location.replace('./cards.html')
        //             }).catch(err => console.log(err))
        // },
        // confirmDeleteCard(id) {
        //     Swal.fire({
        //         title: 'Do You Confirm delete your card?',
        //         showCancelButton: true,
        //         confirmButtonText: 'Confirm',
        //         cancelButtonText: 'Cancel'
        //     }).then((result) => {
        //         if (result.isConfirmed) {
        //           this.deleteCard(id)
        //         }
        //     })
        // }, 
        isCardExpired(date) {
            const today = new Date();
            const expirationDate = new Date(date);
            return expirationDate < today;
          },
        logout() {
            axios.post('/api/logout')
                .then(response => {
                    window.location.replace('./index.html')
                })
                .catch(err => console.log(err));
        },
    }
    ,

});

app.mount('#app');