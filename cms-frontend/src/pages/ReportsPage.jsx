import React, { useState } from 'react'
import { reportsApi } from '../api/api'

const REPORTS = [
    {
        id: 'cards',
        title: 'Cards Report',
        description: 'All cards in the system with limits, status, and update history.',
        icon: (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                <line x1="1" y1="10" x2="23" y2="10" />
            </svg>
        ),
        pdf: () => reportsApi.downloadCardsPdf(),
        csv: () => reportsApi.downloadCardsCsv(),
        pdfFile: 'cards_report.pdf',
        csvFile: 'cards_report.csv',
    },
    {
        id: 'card-requests',
        title: 'Card Requests Report',
        description: 'All activation and deactivation requests with their current status.',
        icon: (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
                <rect x="8" y="2" width="8" height="4" rx="1" ry="1" />
                <line x1="9" y1="12" x2="15" y2="12" />
                <line x1="9" y1="16" x2="15" y2="16" />
                <line x1="9" y1="8" x2="15" y2="8" />
            </svg>
        ),
        pdf: () => reportsApi.downloadCardRequestsPdf(),
        csv: () => reportsApi.downloadCardRequestsCsv(),
        pdfFile: 'card_requests_report.pdf',
        csvFile: 'card_requests_report.csv',
    },
    {
        id: 'approvals',
        title: 'Approvals Report',
        description: 'Approved and rejected requests with approver details.',
        icon: (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="9 11 12 14 22 4" />
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
            </svg>
        ),
        pdf: () => reportsApi.downloadApprovalsPdf(),
        csv: () => reportsApi.downloadApprovalsCsv(),
        pdfFile: 'approvals_report.pdf',
        csvFile: 'approvals_report.csv',
    },
    {
        id: 'audit',
        title: 'Audit Report',
        description: 'System activity logs tracking all user actions and changes.',
        icon: (
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
                <line x1="8" y1="11" x2="14" y2="11" />
                <line x1="11" y1="8" x2="11" y2="14" />
            </svg>
        ),
        pdf: () => reportsApi.downloadAuditPdf(),
        csv: () => reportsApi.downloadAuditCsv(),
        pdfFile: 'audit_report.pdf',
        csvFile: 'audit_report.csv',
    },
]

function ReportsPage() {
    // Track loading/error state per report card per format
    const [states, setStates] = useState({})
    const [globalError, setGlobalError] = useState(null)

    const getState = (id, fmt) => states[`${id}_${fmt}`] || {}

    const setLoading = (id, fmt, loading) =>
        setStates((prev) => ({ ...prev, [`${id}_${fmt}`]: { ...prev[`${id}_${fmt}`], loading } }))

    const setError = (id, fmt, error) =>
        setStates((prev) => ({ ...prev, [`${id}_${fmt}`]: { ...prev[`${id}_${fmt}`], error } }))

    const download = async (report, fmt) => {
        setGlobalError(null)
        setLoading(report.id, fmt, true)
        setError(report.id, fmt, null)
        try {
            const res = fmt === 'pdf' ? await report.pdf() : await report.csv()
            const blob = new Blob([res.data], {
                type: fmt === 'pdf' ? 'application/pdf' : 'text/csv',
            })
            const url = window.URL.createObjectURL(blob)
            const link = document.createElement('a')
            link.href = url
            link.download = fmt === 'pdf' ? report.pdfFile : report.csvFile
            document.body.appendChild(link)
            link.click()
            link.remove()
            window.URL.revokeObjectURL(url)
        } catch (err) {
            const msg =
                err.response?.data?.message ||
                err.message ||
                `Failed to download ${fmt.toUpperCase()} report`
            setError(report.id, fmt, msg)
            setGlobalError(msg)
        } finally {
            setLoading(report.id, fmt, false)
        }
    }

    return (
        <div>
            <div className="card">
                <h2>Reports</h2>
                <p className="subtitle">
                    Download system reports as PDF or CSV. Each report reflects the current data in the
                    database.
                </p>
                {globalError && (
                    <div className="alert alert-error">
                        <strong>Error:</strong> {globalError}
                    </div>
                )}
            </div>

            <div className="reports-grid">
                {REPORTS.map((report) => {
                    const pdfState = getState(report.id, 'pdf')
                    const csvState = getState(report.id, 'csv')
                    return (
                        <div key={report.id} className="report-card">
                            <div className="report-card-icon">{report.icon}</div>
                            <div className="report-card-body">
                                <h3 className="report-card-title">{report.title}</h3>
                                <p className="report-card-desc">{report.description}</p>

                                {(pdfState.error || csvState.error) && (
                                    <div className="alert alert-error" style={{ fontSize: '12px', padding: '0.5rem 0.75rem', marginBottom: '0.75rem' }}>
                                        {pdfState.error || csvState.error}
                                    </div>
                                )}

                                <div className="report-actions">
                                    <button
                                        id={`${report.id}-pdf-btn`}
                                        className="btn btn-report-pdf"
                                        onClick={() => download(report, 'pdf')}
                                        disabled={pdfState.loading || csvState.loading}
                                    >
                                        {pdfState.loading ? (
                                            <>
                                                <span className="spinner" /> Generating…
                                            </>
                                        ) : (
                                            <>
                                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                                                    <polyline points="7 10 12 15 17 10" />
                                                    <line x1="12" y1="15" x2="12" y2="3" />
                                                </svg>
                                                Download PDF
                                            </>
                                        )}
                                    </button>

                                    <button
                                        id={`${report.id}-csv-btn`}
                                        className="btn btn-report-csv"
                                        onClick={() => download(report, 'csv')}
                                        disabled={pdfState.loading || csvState.loading}
                                    >
                                        {csvState.loading ? (
                                            <>
                                                <span className="spinner" /> Generating…
                                            </>
                                        ) : (
                                            <>
                                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                                                    <polyline points="7 10 12 15 17 10" />
                                                    <line x1="12" y1="15" x2="12" y2="3" />
                                                </svg>
                                                Download CSV
                                            </>
                                        )}
                                    </button>
                                </div>
                            </div>
                        </div>
                    )
                })}
            </div>


        </div>
    )
}

export default ReportsPage
