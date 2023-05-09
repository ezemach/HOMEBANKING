const { createApp } = Vue;
createApp({
    data() {
        return {
            data:[],
            externAccount:"",
            ownAccount:"",
            amount:"",
            description:"",
            accounts:"",
            myAccount:"",
            otherAccount:""
        }
    },
    created() {
      this.loadData()
  },
    methods: {
        loadData(){
          axios.get('http://localhost:8080/api/clients/current')
          .then(response => {
              this.data = response.data
              this.accounts = this.data.accounts
              this.accounts1 = this.data.accounts[0]
              this.accounts2 = this.data.accounts[2]
            //   this.cuentas = this.data.accounts
            //   this.prestamos = this.data.loans
              console.log(this.data)
              console.log(this.accounts)
            //   console.log(this.cuentas)
            //   console.log(this.prestamos)
          })
          .catch(err => console.log(err))
        },
        newTransfer() {
            axios.post('/api/clients/current/transactions',`amount=${this.amount}&description=${this.description}&numberOrigin=${this.ownAccount}&numberDestiny=${this.externAccount}`)
                .then(response => { 
                    console.log(response.data)
                    Swal.fire(
                        response.data,
                        'You have created a transaction!',
                        'success'
                      )
                    console.log("transaction")
                    console.log(response.data)
                    
                    
                })
                .catch(err =>{
                    Swal.fire({
                        icon: 'error',
                        title: 'Oops...',
                        text: err.response.data,
                      })
                console.error(err);
                this.error = "failed to create transaction. Please try again!"    
            });     
        },
        ownAcc(){
            this.myAccount = true
            this.otherAccount = false

        },
        forAcc(){
          this.myAccount = false
          this.otherAccount = true
        },
        logout() {
          axios.post('/api/logout')
          .then(response => {
                      window.location.replace('./index.html')
              })
              .catch(err => console.log(err));},
    }

})
.mount('#app')

