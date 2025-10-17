import { useEffect, useState } from 'react'

function App() {
  const [loadedFromSession, setLoadedFromSession] = useState(false)
  const [isRidirect, setIsRidirect] = useState(false)
  const [formData, setFormData] = useState({
    terms: '',
    loanAmount: '',
    interestRate: '',
    residualValue: '',
    accurateAmount: '0',
  })

  useEffect(() => {
    loadFromSession()
  },[])

  useEffect(() => {
    calculatePaymentAmount()
  },[formData.terms,formData.loanAmount,formData.interestRate,formData.residualValue])

  const [errors, setErrors] = useState({})
  
  const loadFromSession = async () => {
    let url = `http://localhost:8080/loan/api/load`
    const response = await fetch(url, {
        method: 'GET',
        credentials: 'include' // 👈 REQUIRED EVERY TIME
      })
    if (!response.ok) {
      throw new Error(`Failed to fetch tasks: ${response.statusText}`)
    }
    const data = await response.json()
    setLoadedFromSession(true)
    Object.keys(data).forEach(key => {
      let value = data[key];
      updateFormData(key, value);
    })
  }

  const validateField = (name, value) => {
    const numValue = parseFloat(value)
    if (!value) return `${name.replace('_', ' ')} is required`
    if (isNaN(numValue) || numValue < 0) return `${name.replace('_', ' ')} must be a positive number`
    return ''
  }

  const calculatePaymentAmount = () => {
    let formValue = formData;
    let intInterestRate = parseInt(formData.interestRate)
    let floatLoanAmount = parseFloat(formData.loanAmount)
    let floatResidualValue = parseFloat(formData.residualValue)
    let actualInterestRate = (intInterestRate/100).toFixed(2);
    let intTerms = parseInt(formData.terms)
    //full formula (([Loan Amount] + [RV]) / 2 * [interest rate] / 12 * term + ([loan amount] - [RV])) / [term]
    //(([Loan Amount] + [RV]) / 2 
    let firstPart = (floatLoanAmount + floatResidualValue)/2;
    // (([Loan Amount] + [RV]) / 2 * [interest rate] / 12 * term
    let secondPart = (firstPart*(actualInterestRate/12))*intTerms;
    //([loan amount] - [RV])) / [term]
    let thirdpart = (floatLoanAmount-floatResidualValue);
    let total = (secondPart+thirdpart);
    let calculateValue=(total/intTerms);
    // let calculateValue = ((formData.loanAmount + formData.residualValue) / 2 * actualInterestRate / 12 * formData.terms + (formData.loanAmount - formData.residualValue)) / formData.terms
    let roundedValue = calculateValue.toFixed(2)
    if(!loadedFromSession){
      updateFormData('accurateAmount',roundedValue)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))

    const error = validateField(name, value)
    setErrors(prev => ({
      ...prev,
      [name]: error
    }))
    setLoadedFromSession(false)
  }

  const updateFormData = (name, value) => {
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const newErrors = {}
    Object.keys(formData).forEach(key => {
      newErrors[key] = validateField(key, formData[key])
    })
    setErrors(newErrors)

    const hasErrors = Object.values(newErrors).some(error => error)
    if (!hasErrors) {
      // All fields valid - you can process the data here
      console.log('Valid form data:', formData)
    }
    let payload = {
          terms: formData.terms,
          loanAmount: formData.loanAmount,
          interestRate: formData.interestRate,
          residualValue: formData.residualValue
        }
    let response = await fetch(`http://localhost:8080/loan/api/save`, {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        })

    if (!response.ok) {
      setIsRidirect(true)
    }
    const data = await response.json()
    updateFormData('accurateAmount', data.accurateAmount)
  }

  const isFormValid = Object.values(errors).every(error => !error)

  return (
     

    <div className="min-h-screen bg-gray-50 py-8">
      
        {isRidirect && (
          <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-6">
            <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
                Thank you for your business!
              </h1>
          </div>
        )}

        {!isRidirect && (
          <div className="max-w-md mx-auto bg-white rounded-lg shadow-md p-6">
            <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">
                Loan Calculator
              </h1>
              <form onSubmit={handleSubmit} className="space-y-4">
              {/* Term Input */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Term (months)
                  </label>
                  <input
                    type="number"
                    name="terms"
                    value={formData.terms}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 ${
                      errors.terms 
                        ? 'border-red-500 focus:ring-red-500' 
                        : 'border-gray-300 focus:ring-blue-500'
                    }`}
                    placeholder="Enter terms in months"
                    min="0"
                    step="1"
                  />
                  {errors.terms && (
                    <p className="mt-1 text-sm text-red-600">{errors.terms}</p>
                  )}
                </div>

                {/* Loan Amount Input */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Loan Amount ($)
                  </label>
                  <input
                    type="number"
                    name="loanAmount"
                    value={formData.loanAmount}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 ${
                      errors.loanAmount 
                        ? 'border-red-500 focus:ring-red-500' 
                        : 'border-gray-300 focus:ring-blue-500'
                    }`}
                    placeholder="Enter loan amount"
                    min="0"
                    step="0.01"
                  />
                  {errors.loanAmount && (
                    <p className="mt-1 text-sm text-red-600">{errors.loanAmount}</p>
                  )}
                </div>

                {/* Interest Rate Input */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Interest Rate (%)
                  </label>
                  <input
                    type="number"
                    name="interestRate"
                    value={formData.interestRate}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 ${
                      errors.interestRate 
                        ? 'border-red-500 focus:ring-red-500' 
                        : 'border-gray-300 focus:ring-blue-500'
                    }`}
                    placeholder="Enter annual rate"
                    min="0"
                    step="0.01"
                  />
                  {errors.interestRate && (
                    <p className="mt-1 text-sm text-red-600">{errors.interestRate}</p>
                  )}
                </div>

                {/* Residual Value Input */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Residual Value ($)
                  </label>
                  <input
                    type="number"
                    name="residualValue"
                    value={formData.residualValue}
                    onChange={handleChange}
                    className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 ${
                      errors.residualValue 
                        ? 'border-red-500 focus:ring-red-500' 
                        : 'border-gray-300 focus:ring-blue-500'
                    }`}
                    placeholder="Enter residual value"
                    min="0"
                    step="0.01"
                  />
                  {errors.residualValue && (
                    <p className="mt-1 text-sm text-red-600">{errors.residualValue}</p>
                  )}
                </div>
                {/* Output View */}
                {isFormValid && Object.values(formData).every(val => val !== '') && (
                  <div className="mt-6 p-4 bg-blue-50 rounded-md">
                    <h2 className="text-lg font-semibold text-gray-800 mb-3">Calculated Payment Amount: {formData.accurateAmount}</h2>
                    <button
                      type="submit"
                      disabled={!isFormValid}
                      className={`w-full py-2 px-4 rounded-md font-medium ${
                        isFormValid
                          ? 'bg-blue-600 hover:bg-blue-700 text-white'
                          : 'bg-gray-400 text-gray-500 cursor-not-allowed'
                      } transition-colors`}
                    >
                      Save
                    </button>
                  </div>
                )}
              </form>
          </div>
            
        )}
    </div>
  )
}

export default App