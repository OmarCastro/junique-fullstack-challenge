import React from 'react';
import {WebPageForm} from "~/components/web-page-form";
import {PageInfoPanel} from "~/components/page-info-panel";
import {request, routes} from "~/utilities/play-route-ajax";


export class MainPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = { info: {
                status: "none"
            }
        };
        this.onSubmit = this.onSubmit.bind(this);
    }

    onSubmit(text){
        this.setState(state => ({
            ...state,
            info: {
                status: "submitting"
            }
        }));
        request(routes.controllers.HomeController.verifyWebPage(text))
            .then(response => response.json())
            .then((response) => {
                this.setState(state => ({
                    ...state,
                    info: response
                }));
            });

    }

    componentDidMount() {
    }

    componentWillUnmount() {
    }

    render() {
        return (
            <div>
                <WebPageForm isSubmitting={this.state.info.status === "submitting"} onSubmit={this.onSubmit}/>
                <PageInfoPanel info={this.state.info}/>
            </div>
        );
    }
}