import React, { useState, useRef, useEffect } from 'react'

const MONTHS = [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
]

function MonthPicker({ value, onChange, error }) {
    const [show, setShow] = useState(false)
    const [viewYear, setViewYear] = useState(new Date().getFullYear())
    const containerRef = useRef(null)

    // Parse initial value (expected YYYY-MM-DD or empty)
    useEffect(() => {
        if (value && value.includes('-')) {
            const parts = value.split('-')
            setViewYear(parseInt(parts[0]))
        }
    }, [value])

    // Close on click outside
    useEffect(() => {
        function handleClickOutside(event) {
            if (containerRef.current && !containerRef.current.contains(event.target)) {
                setShow(false)
            }
        }
        document.addEventListener('mousedown', handleClickOutside)
        return () => document.removeEventListener('mousedown', handleClickOutside)
    }, [])

    const handleMonthSelect = (monthIndex) => {
        const month = (monthIndex + 1).toString().padStart(2, '0')
        onChange(`${viewYear}-${month}-01`)
        setShow(false)
    }

    const navigateYear = (offset) => {
        setViewYear(prev => prev + offset)
    }

    const getDisplayValue = () => {
        if (!value) return 'Select Month'
        const parts = value.split('-')
        const m = parseInt(parts[1]) - 1
        const y = parts[0].slice(-2)
        return `${MONTHS[m]} / ${y}`
    }

    const now = new Date()
    const currentYear = now.getFullYear()
    const currentMonth = now.getMonth()

    return (
        <div className="month-picker-container" ref={containerRef}>
            <div
                className={`form-group-input month-picker-input ${error ? 'input-error' : ''}`}
                onClick={() => setShow(!show)}
                style={{
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-md)',
                    padding: '0.6rem 0.8rem',
                    fontSize: '14px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    backgroundColor: 'white'
                }}
            >
                <span>{getDisplayValue()}</span>
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                    <line x1="16" y1="2" x2="16" y2="6" />
                    <line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                </svg>
            </div>

            {show && (
                <div className="month-picker-popover">
                    <div className="month-picker-header">
                        <button type="button" className="nav-btn" onClick={() => navigateYear(-1)}>&larr;</button>
                        <span className="month-picker-year">{viewYear}</span>
                        <button type="button" className="nav-btn" onClick={() => navigateYear(1)}>&rarr;</button>
                    </div>
                    <div className="month-grid">
                        {MONTHS.map((name, idx) => {
                            const isSelected = value === `${viewYear}-${(idx + 1).toString().padStart(2, '0')}-01`
                            const isDisabled = viewYear < currentYear || (viewYear === currentYear && idx < currentMonth);

                            return (
                                <div
                                    key={name}
                                    className={`month-cell ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}`}
                                    onClick={() => !isDisabled && handleMonthSelect(idx)}
                                >
                                    {name}
                                </div>
                            )
                        })}
                    </div>
                </div>
            )}
        </div>
    )
}

export default MonthPicker
