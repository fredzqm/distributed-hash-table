var React = require('react');

class NamesList extends React.Component {
    constructor(props) {
        super(props)
        this.state = {names: []}
    }

    componentDidMount() {
        this.update()
    }

    render() {
        var names = this.state.names.map(name => <li>{name}</li>)
        return (
            <ul>{names}</ul>
        )
    }

    update() {
        fetch(this.props.url)
            .then(response => response.json())
            .then(names => this.setState({names: names}))
    }
}

module.exports = NamesList