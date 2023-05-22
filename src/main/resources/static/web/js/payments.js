const { createApp } = Vue;
createApp({
    data() {
        return {
            data:[],
            clients:"",
            idLoan:"",
            loans:"",
            accounts:"",
            selectedLoan:"",
            numberAccount:"",
            interests:"",
            payments:""
        }
    },
    created() {
      
      this.loadClientData()
  },
    methods: {
        loadClientData(){
          axios.get('http://localhost:8080/api/clients/current')
          .then(response => {
              this.loans = response.data.loans
              console.log(this.loans)
              this.accounts = response.data.accounts
              console.log(this.accounts)
              this.clients = response.data
              console.log(this.clients)
          })
          .catch(err => console.log(err))
        },
       
        getCurrentPaymentAmount(loan) {
          const paymentAmount = loan.amount / loan.payments;
          return paymentAmount.toFixed(2);
        },
        getCurrentPayment(payments) {
          const totalPayments = 12;
          const currentPayment = payments + 1;
          return `${currentPayment}/${totalPayments}`;
        },
        
        newPayment() {
          const selectedOption = document.querySelector('#payments option:checked');
          this.payments = parseFloat(selectedOption.value);
          this.idLoan = selectedOption.dataset.id;
           console.log({
            
              payments: this.payments,
              number: this.numberAccount,
              id: this.idLoan
          })
            axios.post(`/api/loans/payments`,{
              payments: this.payments,
              number: this.numberAccount,
              id: this.idLoan
          })
                .then(response => { 
                    console.log(response.data)
                    Swal.fire(
                        response.data,
                        'You have paid a Loan!',
                        'success'
                      )
                    console.log("Loan")
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
        confirmPayment() {
          const selectedOption = document.querySelector('#payments option:checked');
          this.payments = parseFloat(selectedOption.value);
          this.idLoan = selectedOption.dataset.id;

          const selectedLoan = this.loans.find(loan => loan.id === this.idLoan);
          const selectedAccount = this.accounts.find(account => account.number === this.numberAccount);
          
          Swal.fire({
              title: 'Do You Confirm a Pay Your Loan?',
              html: `
                  <p>Amount Payment: ${(this.payments)}</p>
                  <p>Account: ${(this.numberAccount) (selectedAccount.balance.toFixed(2))}</p>
                  <p>Account Balance: $${selectedAccount.balance.toFixed(2)}</p>
                  
                  
              `,
              showCancelButton: true,
              confirmButtonText: 'Confirm',
              cancelButtonText: 'Cancel'
          }).then((result) => {
              if (result.isConfirmed) {
                this.newPayment()
              }
          })
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

