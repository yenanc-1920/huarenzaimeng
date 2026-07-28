const OPERATORS = [
  { id: 1, name: 'Grameenphone', prefix: ['017', '013'] },
  { id: 2, name: 'Robi', prefix: ['018', '016'] },
  { id: 3, name: 'Banglalink', prefix: ['019', '014'] },
  { id: 4, name: 'Teletalk', prefix: ['015'] },
  { id: 5, name: 'Airtel', prefix: ['016'] }
]

const PHONE_REGEX = /^01[3-9]\d{8}$/

export function validatePhone(phone) {
  return PHONE_REGEX.test(phone)
}

export function identifyOperator(phone) {
  if (!phone || phone.length < 3) return null
  const prefix = phone.substring(0, 3)
  return OPERATORS.find(op => op.prefix.includes(prefix)) || null
}

export function getOperators() {
  return OPERATORS
}

export function formatPhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1-$2-$3-$4')
}

export function maskPhone(phone) {
  if (!phone || phone.length < 11) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}
