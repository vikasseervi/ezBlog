export const formatDate = (isoString) => {
    if (!isoString) return '';
    const date = new Date(isoString);    
    if (Number.isNaN(date.getTime())) return '';
    return date.toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' });
};